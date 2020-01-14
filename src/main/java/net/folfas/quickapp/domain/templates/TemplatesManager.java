package net.folfas.quickapp.domain.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.QuickAppProperties;
import net.folfas.quickapp.domain.Filesystem;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplatesManager {

    private final TemplatesGitRepository git;
    private final Filesystem filesystem;
    private final TemplateFactory templateFactory;
    private final QuickAppProperties properties;

    private Map<String, Template> templates = new HashMap<>();

    @PostConstruct
    void initialize() throws GitCloneFailedException {
        File templateDirectory = filesystem.createTemplateDirectory();
        git.cloneTemplateRepository(templateDirectory, properties.getTemplates().getGitRepository().getUrl());
        loadTemplates();
    }

    public List<Template> getTemplates() {
        return new ArrayList<>(templates.values());
    }

    public Template getByKey(String templateKey) {
        Assert.hasText(templateKey, "Given template key must not be empty");
        Assert.isTrue(templates.containsKey(templateKey), "Template with key: " + templateKey + " does not exists");
        return templates.get(templateKey);
    }

    public void update() {
        updateGitTemplatesRepository();
        loadTemplates();
    }

    private void updateGitTemplatesRepository() {
        File templateDirectory = filesystem.getTemplateDirectory();
        git.pullTemplateRepository(templateDirectory);
    }

    private void loadTemplates() {
        templates = filesystem.getTemplateDirectoryDirectories().stream()
            .filter(File::isDirectory)
            .filter(templateFactory::isValidTemplate)
            .map(templateFactory::getTemplateFromTemplateDirectory)
            .collect(Collectors.toMap(Template::getKey, Function.identity()));
        log.info("Updated available templates");
    }
}
