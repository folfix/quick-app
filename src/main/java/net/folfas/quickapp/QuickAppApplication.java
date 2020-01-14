package net.folfas.quickapp;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class QuickAppApplication {

    private final QuickAppProperties properties;

    @PostConstruct
    public void verifyProperties() {
        if (properties.isPrintPropertiesOnStartup()) {
            log.info("Application starts with following configuration: {}", properties);
        }

        String gitRepositoryUrl = properties.getTemplates().getGitRepository().getUrl();
        Assert.hasText(gitRepositoryUrl, "Template GIT repository url must be provided");

        boolean publishTokenPresent = StringUtils.hasText(properties.getPublish().getBitbucketRestApi().getBearerToken());
        boolean publishUsernamePresent = StringUtils.hasText(properties.getPublish().getBitbucketRestApi().getUsername());
        boolean publishPasswordPresent = StringUtils.hasText(properties.getPublish().getBitbucketRestApi().getPassword());

        boolean bothAuthMethodUsed = publishTokenPresent && (publishUsernamePresent || publishPasswordPresent);
        Assert.isTrue(!bothAuthMethodUsed, "Do not use username/password and Bearer authentication for Bitbucket in the same time");

        Assert.isTrue(!publishPasswordPresent || publishUsernamePresent, "Provide publish Bitbucket username");
        Assert.isTrue(!publishUsernamePresent || publishPasswordPresent, "Provide publish Bitbucket password");
    }

    public static void main(String[] args) {
        SpringApplication.run(QuickAppApplication.class, args);
    }

}
