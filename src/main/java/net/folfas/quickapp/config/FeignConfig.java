package net.folfas.quickapp.config;

import net.folfas.quickapp.QuickAppApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = QuickAppApplication.class)
public class FeignConfig {
}
