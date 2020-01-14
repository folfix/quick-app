package net.folfas.quickapp.domain.generator.descriptor;

import lombok.Value;
import org.springframework.util.Assert;

@Value
public class DescriptorGitPushRepository {

    String url;
    String token;

    public DescriptorGitPushRepository(String url, String token) {
        Assert.hasText(url, "Given GIT push URL must not be empty");
        Assert.hasText(token, "Given GIT push token must not be empty");

        this.url = url;
        this.token = token;
    }
}
