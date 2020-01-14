package net.folfas.quickapp.integration.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RepositoryForm {
    String name;
    @JsonProperty("public")
    Boolean isPublic;

}
