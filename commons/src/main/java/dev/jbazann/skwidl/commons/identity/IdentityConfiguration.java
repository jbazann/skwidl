package dev.jbazann.skwidl.commons.identity;

import dev.jbazann.skwidl.commons.config.AsyncConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityConfiguration {

    @Value("${spring.application.name}")
    private String APPLICATION_NAME = "";

    @Bean
    public ApplicationMemberRegistry applicationMemberRegistry(AsyncConfigurationProperties config) {
        String identity = getIdentity(config);
        return new ApplicationMemberRegistry(
                new ApplicationMember(identity),
                new ApplicationMember(config.getDefaultCoordinator())
        );
    }

    private String getIdentity(AsyncConfigurationProperties config) {
        String identity = config.getIdentity();
        if(identity == null || identity.isEmpty()) {
            identity = APPLICATION_NAME;
        }
        if(identity == null || identity.isEmpty()) {
            throw new IllegalStateException("Failed to load application name and commons identity.");
        }
        return identity;
    }

}
