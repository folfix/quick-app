package net.folfas.quickapp.integration;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.domain.generator.descriptor.DescriptorGitPushRepository;
import net.folfas.quickapp.domain.publish.PublishGitVersionControl;
import net.folfas.quickapp.domain.publish.VersionControlProject;
import net.folfas.quickapp.domain.publish.VersionControlRepository;
import net.folfas.quickapp.integration.bitbucket.BitbucketFeignClient;
import net.folfas.quickapp.integration.bitbucket.model.ProjectDto;
import net.folfas.quickapp.integration.bitbucket.model.RepositoryForm;
import net.folfas.quickapp.integration.git.GitNativeConsole;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VersionControlImpl implements PublishGitVersionControl {

    private final BitbucketFeignClient api;
    private final GitNativeConsole gitConsole;

    @Override
    public List<VersionControlProject> projects() {
        List<ProjectDto> bitbucketProjects = api.projects().getValues();
        return bitbucketProjects
            .stream()
            .map(ProjectDto::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public VersionControlRepository addRepository(String projectKey, String repositoryName) {
        return api.addRepository(projectKey, new RepositoryForm(repositoryName, false)).toDomain();
    }

    @Override
    public void pushRepository(VersionControlRepository repository, File sourceLocation) {
        gitConsole.push(repository.getCloneUrl(), sourceLocation);
    }

    @Override
    public void pushRepository(DescriptorGitPushRepository gitPushRepository, File sourceLocation) {
        gitConsole.push(gitPushRepository.getUrl(), gitPushRepository.getToken(), sourceLocation);
    }
}
