package net.folfas.quickapp.api.rest;

import lombok.Value;
import net.folfas.quickapp.domain.templates.manifest.ManifestChoiceVariable;

@Value
class ChoiceVariableDto {

    private String id;
    private String name;
    private String description;
    private boolean enabledByDefault;

    ChoiceVariableDto(ManifestChoiceVariable manifestVariable) {
        this.id = manifestVariable.getId();
        this.name = manifestVariable.getName();
        this.description = manifestVariable.getDescription();
        this.enabledByDefault = manifestVariable.isEnabledByDefault();
    }
}
