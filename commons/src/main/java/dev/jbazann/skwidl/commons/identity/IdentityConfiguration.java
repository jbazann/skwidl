package dev.jbazann.skwidl.commons.identity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityConfiguration {

    @Value("${spring.application.name}")
    private String APPLICATION_NAME = "";

    @Bean
    @ConditionalOnMissingBean
    public ApplicationMember missingIdentity() {
        if(APPLICATION_NAME.isEmpty()) throw new IllegalStateException("Failed to load application name.");
        return new ApplicationMember(APPLICATION_NAME);
    }

}
