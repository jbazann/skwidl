package dev.jbazann.skwidl.commons.identity;

import dev.jbazann.skwidl.commons.config.AsyncConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class IdentityConfiguration {

    @Value("${spring.application.name}")
    private String APPLICATION_NAME = "";

    @Bean(name = "identity")
    @Primary
    @ConditionalOnMissingBean(name = "identity")
    public ApplicationMember identity(AsyncConfigurationProperties config) {
        String identity = config.getIdentity();
        if(identity == null || identity.isEmpty()) {
            identity = APPLICATION_NAME;
        }
        if(identity == null || identity.isEmpty()) {
            throw new IllegalStateException("Failed to load application name or commons identity.");
        }
        return new ApplicationMember(APPLICATION_NAME);
    }

    @Bean(name = "defaultCoordinator")
    @ConditionalOnMissingBean(name = "defaultCoordinator")
    public ApplicationMember defaultCoordinator(AsyncConfigurationProperties config) {
        return new ApplicationMember(config.getDefaultCoordinator());
    }

}
