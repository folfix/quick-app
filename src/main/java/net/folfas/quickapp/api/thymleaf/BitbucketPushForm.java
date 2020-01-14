package net.folfas.quickapp.api.thymleaf;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BitbucketPushForm {

    @NotEmpty
    private String projectKey;
    @NotEmpty
    private String repositoryName;
    @NotEmpty
    private String resultId;
}
