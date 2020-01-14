package net.folfas.quickapp.api.rest;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
import net.folfas.quickapp.domain.templates.Template;

@Value
class TemplateDto {

    String key;
    String name;
    String description;
    List<String> tags;
    List<VariableDto> variables;
    List<ChoiceVariableDto> choiceVariables;

    TemplateDto(Template template) {
        this.key = template.getKey();
        this.name = template.getManifest().getName();
        this.description = template.getManifest().getDescription();
        this.tags = template.getManifest().getTags();
        this.variables = template.getManifest().getVariables().stream().map(VariableDto::new).collect(Collectors.toList());
        this.choiceVariables = template.getManifest().getChoiceVariables().stream().map(ChoiceVariableDto::new).collect(Collectors.toList());
    }
}
