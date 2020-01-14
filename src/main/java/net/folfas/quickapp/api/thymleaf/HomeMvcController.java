package net.folfas.quickapp.api.thymleaf;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.templates.Template;
import net.folfas.quickapp.domain.templates.TemplatesManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeMvcController {

    private final TemplatesManager templatesManager;

    @GetMapping
    public String home(Model model) {
        templatesManager.update();
        List<Template> templates = templatesManager.getTemplates();

        model.addAttribute("templates", templates);
        return "home";
    }
}
