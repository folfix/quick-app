package net.folfas.quickapp.integration.bitbucket.model;

import javax.validation.UnexpectedTypeException;
import lombok.Data;
import net.folfas.quickapp.domain.publish.VersionControlRepository;

@Data
public class RepositoryDto {
    private Long id;
    private String slug;
    private String name;
    private ProjectDto project;
    private RepositoryLinksDto links;

    public VersionControlRepository toDomain() {
        return new VersionControlRepository(id, slug, name, project.toDomain(), url(), cloneUrl());
    }

    private String url() {
        return links.self.stream().findAny().map(RepositoryLinkDto::getHref).orElseThrow(() -> new RuntimeException("Failed to find url link in Bitbucket repository"));
    }

    private String cloneUrl() {
        return links.clone.stream()
            .filter(link -> link.getName().startsWith(RepositoryLinkDto.HTTP_CLONE_URL_NAME))
            .findAny()
            .map(RepositoryLinkDto::getHref)
            .orElseThrow(() -> new UnexpectedTypeException("Bitbucket does not support http nor https push"));
    }
}