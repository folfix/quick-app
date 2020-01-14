package net.folfas.quickapp.integration.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.QuickAppProperties;
import net.folfas.quickapp.domain.templates.GitCloneFailedException;
import net.folfas.quickapp.domain.templates.TemplatesGitRepository;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitNativeConsole implements TemplatesGitRepository {

    private final QuickAppProperties properties;

    @Getter
    private Health health = Health.unknown().build();

    @PostConstruct
    public void ignoreSslConfiguration() throws Exception {
        QuickAppProperties.GitRepository templatesRepository = properties.getTemplates().getGitRepository();
        if (templatesRepository.isDisableSslVerification()) {
            disableSSLVerify(new URI(templatesRepository.getUrl()));
            log.info("Disabled SSL verification for {}", templatesRepository.getUrl());
        }

        QuickAppProperties.GitRepository publishRepository = properties.getPublish().getBitbucketRestApi();
        log.info(publishRepository.toString());
        if (StringUtils.hasText(publishRepository.getUrl()) &&
            publishRepository.isDisableSslVerification()) {
            disableSSLVerify(new URI(publishRepository.getUrl()));
            log.info("Disabled SSL verification for {}", templatesRepository.getUrl());
        }
    }

    @Override
    public void cloneTemplateRepository(File destination, String templatesGitRepositoryUrl) throws GitCloneFailedException {
        log.info("Cloning template repository");
        try {
            CloneCommand cloneCommand = Git
                .cloneRepository()
                .setURI(templatesGitRepositoryUrl)
                .setDirectory(destination);
            withAuthentication(properties.getTemplates().getGitRepository(), cloneCommand).call();
            log.info("Cloned template repository");
            health = Health.up().build();
        } catch (GitAPIException e) {
            log.error("Make sure that GIT repository exists and you have rights to it: {}", templatesGitRepositoryUrl);
            health = Health.down(e).build();
            throw new GitCloneFailedException("Failed to clone repository: " + templatesGitRepositoryUrl, e);
        }
    }

    @Override
    public void pullTemplateRepository(File repositoryLocation) {
        log.info("Pulling template repository");
        try {
            FetchCommand fetchCommand = Git.open(repositoryLocation).fetch().setForceUpdate(true);
            withAuthentication(properties.getTemplates().getGitRepository(), fetchCommand).call();
            Git.open(repositoryLocation).reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/master").call();
            log.info("Pulled template repository");
            health = Health.up().build();
        } catch (GitAPIException | IOException e) {
            health = Health.down(e).build();
            throw new RuntimeException("Failed to pull repository: " + repositoryLocation, e);
        }
    }

    public void push(String cloneUrl, File tempDir) {
        log.info("Starting git push");
        try (Git git = Git.init().setDirectory(tempDir).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(properties.getPublish().getInitialCommitMessage())
                .setAuthor(properties.getPublish().getInitialCommitAuthor(), properties.getPublish().getInitialCommitEmail())
                .setCommitter(properties.getPublish().getInitialCommitAuthor(), properties.getPublish().getInitialCommitEmail())
                .call();
            PushCommand pushCommand = git.push().setRemote(cloneUrl);
            withAuthentication(properties.getPublish().getBitbucketRestApi(), pushCommand).call();
            log.info("Pushed repository");
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to push repository: " + cloneUrl);
        }
    }

    public void push(String cloneUrl, String token, File tempDir) {
        log.info("Starting git push");
        try (Git git = Git.init().setDirectory(tempDir).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(properties.getPublish().getInitialCommitMessage())
                .setAuthor(properties.getPublish().getInitialCommitAuthor(), properties.getPublish().getInitialCommitEmail())
                .setCommitter(properties.getPublish().getInitialCommitAuthor(), properties.getPublish().getInitialCommitEmail())
                .call();

            if (properties.getPublish().getBitbucketRestApi().isDisableSslVerification()) {
                disableSSLVerify(new URI(cloneUrl));
            }

            git.push().setRemote(cloneUrl).setTransportConfigCallback(getAuthorization(token)).call();
            log.info("Pushed repository");
        } catch (Exception e) {
            throw new RuntimeException("Failed to push repository: " + cloneUrl, e);
        }
    }

    private GitCommand withAuthentication(QuickAppProperties.GitRepository templatesRepoProps, TransportCommand command) {
        if (templatesRepoProps.isBearerTokenAuthEnabled()) {
            return command.setTransportConfigCallback(getAuthorization(templatesRepoProps.getBearerToken()));
        }
        if (templatesRepoProps.isUsernamePasswordAuthEnabled()) {
            return command.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                templatesRepoProps.getUsername(),
                templatesRepoProps.getPassword()
            ));
        }
        return command;
    }

    private TransportConfigCallback getAuthorization(String bearerToken) {
        return transport -> {
            if (transport instanceof TransportHttp) {
                TransportHttp myTransport = (TransportHttp) transport;
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + bearerToken);
                myTransport.setAdditionalHeaders(headers);
            }
        };
    }

    private void disableSSLVerify(URI gitServer) throws Exception {
        if (gitServer.getScheme().equals("https")) {
            FileBasedConfig config = SystemReader.getInstance().openUserConfig(null, FS.DETECTED);
            config.load();
            config.setBoolean(
                "http",
                "https://" + gitServer.getHost() + ':' + (gitServer.getPort() == -1 ? 443 : gitServer.getPort()),
                "sslVerify", false);
            config.save();
        }
    }
}
