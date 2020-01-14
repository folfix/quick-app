package net.folfas.quickapp.integration.git;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplatesRepositoryHealthIndicator implements HealthIndicator {

    private final GitNativeConsole console;

    @Override
    public Health health() {
        return console.getHealth();
    }
}
