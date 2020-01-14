package net.folfas.quickapp.integration.bitbucket.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ProjectListDto {
    private Integer size;
    private Integer limit;
    private Integer start;
    private Boolean isLastPage;
    private List<ProjectDto> values = new ArrayList<>();
}
