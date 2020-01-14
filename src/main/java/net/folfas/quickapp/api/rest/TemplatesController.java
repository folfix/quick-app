package net.folfas.quickapp.api.rest;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.templates.TemplatesManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/templates")
@RequiredArgsConstructor
public class TemplatesController {

    private final TemplatesManager templatesManager;

    @GetMapping
    public List<TemplateDto> getAll() {
        templatesManager.update();
        return templatesManager.getTemplates()
            .stream()
            .map(TemplateDto::new)
            .collect(Collectors.toList());
    }

    @GetMapping("/{templateKey}")
    public TemplateDto getOne(@PathVariable String templateKey) {
        return new TemplateDto(templatesManager.getByKey(templateKey));
    }
}
