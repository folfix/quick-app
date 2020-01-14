package net.folfas.quickapp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Data
@Configuration
@ConfigurationProperties(prefix = "quick-app")
public class QuickAppProperties {

    /**
     * (Required) Used to define where quick-app can find templates.
     */
    private Templates templates = new Templates();

    /**
     * (Optional) Used in case that quick-app is used for publishing generated project.
     */
    private Publish publish = new Publish();

    /**
     * Whenever to print properties or not when quick-app starts.
     */
    private boolean printPropertiesOnStartup;

    @Data
    public class Templates {
        private GitRepository gitRepository = new GitRepository();
    }

    @Data
    public class Publish {
        private GitRepository bitbucketRestApi = new GitRepository();
        private String initialCommitMessage;
        private String initialCommitAuthor;
        private String initialCommitEmail;
    }

    @Data
    public class GitRepository {
        private String url;
        private String bearerToken;
        private String username;
        private String password;
        private boolean disableSslVerification;

        public boolean isBearerTokenAuthEnabled() {
            return StringUtils.hasText(bearerToken);
        }

        public boolean isUsernamePasswordAuthEnabled() {
            return StringUtils.hasText(username) && StringUtils.hasText(password);
        }
    }
}
