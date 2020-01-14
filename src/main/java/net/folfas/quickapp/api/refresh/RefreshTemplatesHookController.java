package net.folfas.quickapp.api.refresh;

import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.templates.TemplatesManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh-templates")
@RequiredArgsConstructor
public class RefreshTemplatesHookController {

    private final TemplatesManager templatesManager;

    @PostMapping
    public void refresh() {
        templatesManager.update();
    }
}
