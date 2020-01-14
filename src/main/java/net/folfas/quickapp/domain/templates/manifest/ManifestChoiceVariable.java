package net.folfas.quickapp.domain.templates.manifest;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class ManifestChoiceVariable {

    private String id;
    private String name;
    private String description;
    private boolean enabledByDefault = false;
    private Set<String> filesToCopy = new HashSet<>();
}
