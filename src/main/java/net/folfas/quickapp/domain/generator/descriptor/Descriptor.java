package net.folfas.quickapp.domain.generator.descriptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Value;
import net.folfas.quickapp.domain.templates.Template;
import net.folfas.quickapp.domain.templates.manifest.Manifest;
import net.folfas.quickapp.domain.templates.manifest.ManifestChoiceVariable;
import net.folfas.quickapp.domain.templates.manifest.ManifestVariable;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Value
public class Descriptor {

    Template template;
    Set<DescriptorVariable> variables;
    Set<DescriptorChoiceVariable> choiceVariables;
    DescriptorGitPushRepository gitPushRepository;

    public Descriptor(Template template, @Nullable Map<String, String> variables, Map<String, Boolean> choiceVariables) {
        this.template = template;
        this.variables = new HashSet<>();
        Optional.ofNullable(variables).ifPresent(presentVariables -> {
            presentVariables.forEach((variableId, value) -> this.variables.add(new DescriptorVariable(variableId, value)));
        });
        this.choiceVariables = new HashSet<>();
        Optional.ofNullable(choiceVariables).ifPresent(presentVariables -> {
            presentVariables.forEach((variableId, value) -> this.choiceVariables.add(new DescriptorChoiceVariable(variableId, value)));
        });

        this.gitPushRepository = null;
    }

    private Descriptor(
        Template template,
        Set<DescriptorVariable> variables,
        Set<DescriptorChoiceVariable> choiceVariables,
        DescriptorGitPushRepository gitPushRepository
    ) {
        this.template = template;
        this.variables = variables;
        this.choiceVariables = choiceVariables;
        this.gitPushRepository = gitPushRepository;
    }

    public Descriptor withGitPush(String gitUrl, String gitToken) {
        return new Descriptor(template, variables, choiceVariables, new DescriptorGitPushRepository(gitUrl, gitToken));
    }

    public Map<String, String> getMustacheModel() {
        Map<String, String> mustacheModel = getVariablesWithDefaultValues();
        mustacheModel.putAll(getVariablesFromInput());

        verifyVariables(mustacheModel);

        return mustacheModel;
    }

    public List<DescriptorDestinationFile> getDescriptorDestinationFiles() throws IOException {
        Set<Path> filesDisabledByChoiceVariables = getFilesDisabledByChoiceVariables(template.getTemplateDirectory());
        return Files.walk(template.getTemplateDirectory().toPath())
            .filter(path -> !path.toString().endsWith(Manifest.MANIFEST_FILENAME))
            .filter(Files::isRegularFile)
            .filter(path -> !filesDisabledByChoiceVariables.contains(path))
            .map(path -> new DescriptorDestinationFile(path, this, template))
            .collect(Collectors.toList());
    }

    private Map<String, String> getVariablesWithDefaultValues() {
        return template.getManifest().getVariables().stream()
            .filter(ManifestVariable::hasDefaultValue)
            .collect(Collectors.toMap(ManifestVariable::getId, ManifestVariable::getDefaultValue));
    }

    private Map<String, String> getVariablesFromInput() {
        return variables.stream()
            .filter(inputVariable -> template.getManifest().hasVariableWithId(inputVariable.getId()))
            .collect(Collectors.toMap(DescriptorVariable::getId, DescriptorVariable::getValue));
    }

    private Map<ManifestChoiceVariable, Boolean> getChoiceVariablesWithInitialValues() {
        return template.getManifest().getChoiceVariables().stream()
            .collect(Collectors.toMap(Function.identity(), ManifestChoiceVariable::isEnabledByDefault));
    }

    private Set<Path> getFilesDisabledByChoiceVariables(File templateDirectory) {
        Map<ManifestChoiceVariable, Boolean> initialValues = getChoiceVariablesWithInitialValues();
        Map<String, ManifestChoiceVariable> keys = new HashMap<>();
        initialValues.forEach((variable, value) -> keys.put(variable.getId(), variable));

        choiceVariables.forEach(inputVariable -> {
            if (!keys.containsKey(inputVariable.getId())) {
                throw new RuntimeException("Given choice variable does not exists in manifest: " + inputVariable);
            }
        });

        Map<String, Boolean> keysWithValues = new HashMap<>();
        initialValues.forEach((variable, value) -> keysWithValues.put(variable.getId(), variable.isEnabledByDefault()));
        choiceVariables.forEach((variable) -> keysWithValues.put(variable.getId(), variable.getValue()));

        Set<String> disabledVariableKeys = new HashSet<>();
        keysWithValues.forEach((variable, value) -> {
            if (!value) {
                disabledVariableKeys.add(variable);
            }
        });

        Set<String> disabledFiles = new HashSet<>();

        disabledVariableKeys.forEach(key -> {
            disabledFiles.addAll(keys.get(key).getFilesToCopy());
        });

        return disabledFiles.stream().map(fileName -> Path.of(FileUtils.pathToString(templateDirectory), fileName)).collect(Collectors.toSet());
    }

    private void verifyVariables(Map<String, String> mustacheModel) {
        boolean areAllVariablesProvided = template.getManifest().getVariables().size() == mustacheModel.size();
        String errorMessage = "It's required to provide all variables without default value. Extra variables are not allowed too.";
        Assert.isTrue(areAllVariablesProvided, errorMessage);

        template.getManifest().getVariables().forEach(variable -> {
            if (!mustacheModel.containsKey(variable.getId())) {
                throw new RuntimeException("Given variable does not exists in manifest: " + variable.getId());
            }
        });

        mustacheModel.forEach((key, value) -> {
            Assert.hasText(value, "Variable value is empty: " + key);
        });
    }
}
