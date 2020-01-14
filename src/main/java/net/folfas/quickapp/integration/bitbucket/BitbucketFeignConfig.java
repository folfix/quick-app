package net.folfas.quickapp.integration.bitbucket;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.QuickAppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@RequiredArgsConstructor
public class BitbucketFeignConfig {

    private final QuickAppProperties properties;

    @Bean
    @ConditionalOnProperty("quick-app.publish.bitbucket-rest-api.bearer-token")
    public RequestInterceptor bbRequestInterceptor() {
        log.info("Using Bearer authentication for Bitbucket publish");
        return template -> template.header("Authorization", "Bearer " + properties.getPublish().getBitbucketRestApi().getBearerToken());
    }

    @Bean
    @ConditionalOnProperty({"quick-app.publish.bitbucket-rest-api.username", "quick-app.publish.bitbucket-rest-api.password"})
    public BasicAuthRequestInterceptor bbBasicAuthRequestInterceptor() {
        log.info("Using username/password authentication for Bitbucket publish");
        return new BasicAuthRequestInterceptor(
            properties.getPublish().getBitbucketRestApi().getUsername(),
            properties.getPublish().getBitbucketRestApi().getPassword());
    }

    @Bean
    Retryer bitbucketRetryer() {
        return new Retryer.Default();
    }
}
