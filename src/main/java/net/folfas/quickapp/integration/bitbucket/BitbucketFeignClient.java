package net.folfas.quickapp.integration.bitbucket;

import net.folfas.quickapp.integration.bitbucket.model.ProjectListDto;
import net.folfas.quickapp.integration.bitbucket.model.RepositoryDto;
import net.folfas.quickapp.integration.bitbucket.model.RepositoryForm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ConditionalOnProperty("quick-app.publish.bitbucket-rest-api.url")
@FeignClient(name = "bitbucket-publish-feign-client", url = "${quick-app.publish.bitbucket-rest-api.url}", configuration = BitbucketFeignConfig.class)
public interface BitbucketFeignClient {

    @GetMapping("/projects")
    ProjectListDto projects();

    @PostMapping("/projects/{projectKey}/repos")
    RepositoryDto addRepository(@PathVariable("projectKey") String projectKey, @RequestBody RepositoryForm repositoryForm);
}