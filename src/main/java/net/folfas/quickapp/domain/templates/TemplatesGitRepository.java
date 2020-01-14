package net.folfas.quickapp.domain.templates;

import java.io.File;

public interface TemplatesGitRepository {

    void cloneTemplateRepository(File destination, String templatesGitRepositoryUrl) throws GitCloneFailedException;

    void pullTemplateRepository(File repositoryLocation);
}
