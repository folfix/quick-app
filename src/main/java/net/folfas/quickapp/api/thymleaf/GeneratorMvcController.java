package net.folfas.quickapp.api.thymleaf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.generator.Generator;
import net.folfas.quickapp.domain.generator.GeneratorResult;
import net.folfas.quickapp.domain.generator.ResultsStorage;
import net.folfas.quickapp.domain.generator.descriptor.Descriptor;
import net.folfas.quickapp.domain.publish.PublishGitVersionControl;
import net.folfas.quickapp.domain.publish.VersionControlRepository;
import net.folfas.quickapp.domain.templates.Template;
import net.folfas.quickapp.domain.templates.TemplatesManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/generator")
@RequiredArgsConstructor
public class GeneratorMvcController {

    private final PublishGitVersionControl vcsClient;
    private final TemplatesManager templatesManager;
    private final Generator generator;
    private final ResultsStorage resultsStorage;

    @GetMapping("/{templateId}")
    public String generatorForm(ModelMap modelMap, @PathVariable("templateId") String templateId) {
        templatesManager.update();
        Template template = templatesManager.getByKey(templateId);
        modelMap.addAttribute("template", template);
        modelMap.addAttribute("variables", template.getManifest().getVariables());
        modelMap.addAttribute("choiceVariables", template.getManifest().getChoiceVariables());

        HashMap<String, String> variables = new HashMap<>();
        HashMap<String, Boolean> choiceVariables = new HashMap<>();

        template.getManifest().getVariables().forEach(variable -> variables.put(variable.getId(), variable.getDefaultValue()));
        template.getManifest().getChoiceVariables().forEach(variable -> choiceVariables.put(variable.getId(), variable.isEnabledByDefault()));

        GeneratorForm generatorForm = new GeneratorForm();

        generatorForm.setVariables(variables);
        generatorForm.setChoiceVariables(choiceVariables);

        modelMap.addAttribute("generatorForm", generatorForm);

        return "generator";
    }

    @PostMapping
    public String processMapForm(ModelMap modelMap, @Valid @ModelAttribute(value = "generatorForm") GeneratorForm generatorForm, HttpServletRequest request)
        throws IOException {
        Map<String,Boolean> notNulChoiceVariables = new HashMap<>();
        generatorForm.getChoiceVariables().forEach((variableId, value) -> notNulChoiceVariables.put(variableId, value != null ? value : false));
        Template template = templatesManager.getByKey(generatorForm.getTemplateId());
        Descriptor descriptor = new Descriptor(template, generatorForm.getVariables(), notNulChoiceVariables);
        GeneratorResult result = generator.generate(descriptor);
        modelMap.addAttribute("resultId", result.getResultId());
        modelMap.addAttribute("projects", vcsClient.projects());
        modelMap.addAttribute("intellijUrl", getIntellijUrl(request, result.getResultId()));
        return "success";
    }

    @PostMapping("/push")
    public ModelAndView pushProjectToBitbucket(@Valid @ModelAttribute(value = "bitbucketPushForm") BitbucketPushForm form) {
        GeneratorResult result = resultsStorage.get(form.getResultId());
        Assert.notNull(result, "Unknown generator result id");
        VersionControlRepository repository = vcsClient.addRepository(form.getProjectKey(), form.getRepositoryName());
        vcsClient.pushRepository(repository, result.getGeneratedDirectory());
        return new ModelAndView("redirect:" + repository.getUrl());
    }

    private String getIntellijUrl(HttpServletRequest request, String resultId) throws MalformedURLException {
        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        return requestURL.getProtocol() + "://" + requestURL.getHost() + port + "/intellij/" + resultId;
    }
}
