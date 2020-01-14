package net.folfas.quickapp.integration.bitbucket.model;

import lombok.Data;
import net.folfas.quickapp.domain.publish.VersionControlProject;

@Data
public class ProjectDto {
    private Long id;
    private String key;
    private String name;

    public VersionControlProject toDomain() {
        return new VersionControlProject(id, key, name);
    }
}