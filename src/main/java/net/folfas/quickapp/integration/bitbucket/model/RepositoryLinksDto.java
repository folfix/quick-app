package net.folfas.quickapp.integration.bitbucket.model;

import java.util.List;
import lombok.Data;

@Data
class RepositoryLinksDto {
    List<RepositoryLinkDto> clone;
    List<RepositoryLinkDto> self;
}
