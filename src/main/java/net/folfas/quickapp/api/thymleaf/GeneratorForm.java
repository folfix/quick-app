package net.folfas.quickapp.api.thymleaf;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GeneratorForm {

    private Map<String,String> variables = new HashMap<>();
    private Map<String,Boolean> choiceVariables = new HashMap<>();

    @NotEmpty
    private String templateId;
}
