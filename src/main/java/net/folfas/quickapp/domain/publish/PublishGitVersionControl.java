package net.folfas.quickapp.domain.publish;

import java.io.File;
import java.util.List;
import net.folfas.quickapp.domain.generator.descriptor.DescriptorGitPushRepository;

public interface PublishGitVersionControl {

    List<VersionControlProject> projects();

    VersionControlRepository addRepository(String projectKey, String repositoryName);

    void pushRepository(VersionControlRepository repository, File sourceLocation);

    void pushRepository(DescriptorGitPushRepository gitPushRepository, File sourceLocation);
}
