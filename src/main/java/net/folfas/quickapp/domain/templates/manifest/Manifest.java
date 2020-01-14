package net.folfas.quickapp.domain.templates.manifest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.util.Assert;

@Data
public class Manifest {

    public static final String MANIFEST_FILENAME = "quick-app-manifest.json";

    private String name;
    private String description;
    private List<String> tags = new ArrayList<>();
    private List<ManifestVariable> variables = new ArrayList<>();
    private List<ManifestChoiceVariable> choiceVariables = new ArrayList<>();
    private List<ManifestPathsOverrides> pathsOverrides = new ArrayList<>();
    private Set<String> justCopy = new HashSet<>();

    public boolean hasVariableWithId(String variableId) {
        Assert.hasText(variableId, "Variable id must not be empty");
        return variables.stream().anyMatch(manifestVariableId -> variableId.equals(manifestVariableId.getId()));
    }

    public void validate() {
        Assert.hasText(name, "Name in Manifest must be provided");
        Assert.hasText(description, "Description in Manifest must be provided [manifestName=" + name + "]");

        List<String> availableVariables = variables.stream().map(ManifestVariable::getId).collect(Collectors.toList());
        pathsOverrides.forEach(path -> {
            Assert.isTrue(availableVariables.contains(path.getFromVariable()),
                "Directory Path variable must be mapped to existing variable [manifestName=" + name + ", notFoundVariable: " + path.getFromVariable() + "]");
        });
    }
}
