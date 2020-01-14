package net.folfas.quickapp.domain.templates.manifest;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class ManifestPathsOverrides {
    private String fromVariable;
    private String directoriesSeparator;

    public boolean hasSeparator() {
        return StringUtils.hasText(directoriesSeparator);
    }
}
