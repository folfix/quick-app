package net.folfas.quickapp.api.rest;

import lombok.Value;
import net.folfas.quickapp.domain.templates.manifest.ManifestVariable;

@Value
class VariableDto {

    private String id;
    private String name;
    private String description;
    private String defaultValue;

    VariableDto(ManifestVariable manifestVariable) {
       this.id = manifestVariable.getId();
       this.name = manifestVariable.getName();
       this.description = manifestVariable.getDescription();
       this.defaultValue = manifestVariable.getDefaultValue();
    }
}
