package net.folfas.quickapp.api.rest;

import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/health")
class HealthController {

    @GetMapping
    public Health healthProbe() {
        return Health.up().build();
    }
}
