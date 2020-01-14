package net.folfas.quickapp.api.rest;

import java.io.IOException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.generator.Generator;
import net.folfas.quickapp.domain.generator.GeneratorResult;
import net.folfas.quickapp.domain.generator.descriptor.Descriptor;
import net.folfas.quickapp.domain.templates.Template;
import net.folfas.quickapp.domain.templates.TemplatesManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final TemplatesManager templatesManager;
    private final Generator generator;

    @PostMapping
    public ResponseEntity generate(@RequestBody @Valid GeneratorForm form) {
        Template template = templatesManager.getByKey(form.getTemplateKey());
        Descriptor descriptor = new Descriptor(template, form.getVariables(), form.getChoiceVariables())
            .withGitPush(form.getGitRepositoryUrl(), form.getGitRepositoryToken());
        try {
            GeneratorResult result = generator.generate(descriptor);
            return ResponseEntity.ok(new GeneratorResultDto(result));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
