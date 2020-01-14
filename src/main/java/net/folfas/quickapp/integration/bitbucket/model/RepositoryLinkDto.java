package net.folfas.quickapp.integration.bitbucket.model;

import lombok.Data;

@Data
public class RepositoryLinkDto {
    public static final String HTTP_CLONE_URL_NAME = "http";

    private String href;
    private String name;
}