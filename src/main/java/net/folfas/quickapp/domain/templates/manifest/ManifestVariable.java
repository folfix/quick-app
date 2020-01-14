package net.folfas.quickapp.domain.templates.manifest;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class ManifestVariable {

    private String id;
    private String name;
    private String description;
    private String defaultValue;

    public boolean hasDefaultValue() {
        return StringUtils.hasText(defaultValue);
    }

}
