package net.folfas.quickapp.integration.bitbucket;

import net.folfas.quickapp.integration.bitbucket.model.ProjectListDto;
import net.folfas.quickapp.integration.bitbucket.model.RepositoryDto;
import net.folfas.quickapp.integration.bitbucket.model.RepositoryForm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Used in case of not using Bitbucket publish
 */
@Configuration
public class BitbucketClientMock {

    @Bean
    @ConditionalOnMissingBean
    public BitbucketFeignClient bitbucketMockClient() {
        return new BitbucketFeignClient() {

            @Override
            public ProjectListDto projects() {
                return new ProjectListDto();
            }

            @Override
            public RepositoryDto addRepository(String projectKey, RepositoryForm repositoryForm) {
                throw new RuntimeException("Mock implementation is used, do not invoke this method");
            }
        };
    }
}
