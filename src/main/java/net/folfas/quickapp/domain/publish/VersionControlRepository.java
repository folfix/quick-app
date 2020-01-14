package net.folfas.quickapp.domain.publish;

import lombok.Value;

@Value
public class VersionControlRepository {
    Long id;
    String key;
    String displayName;
    VersionControlProject project;
    String url;
    String cloneUrl;
}
