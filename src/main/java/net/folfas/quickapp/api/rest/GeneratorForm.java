package net.folfas.quickapp.api.rest;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
class GeneratorForm {

    @NotBlank
    private String templateKey;

    private Map<String, String> variables;

    private Map<String, Boolean> choiceVariables;

    @NotBlank
    private String gitRepositoryUrl;

    @NotBlank
    private String gitRepositoryToken;
}
