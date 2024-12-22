package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEventTracer;
import com.jbazann.commons.async.rabbitmq.RabbitConfiguration;
import com.jbazann.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RabbitConfiguration.class)
public class OrchestrationBaseConfiguration {

    @Value("${spring.application.name}")
    private String APPLICATION_NAME = "";

    @Bean
    @ConditionalOnMissingBean
    public ApplicationMember missingIdentity() {
        if(APPLICATION_NAME.isEmpty()) throw new IllegalStateException("Failed to load application name.");
        return new ApplicationMember(APPLICATION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainEventTracer standardDomainEventTracer(ApplicationMember identity, RabbitPublisher publisher) {
        return new DomainEventTracer(identity, publisher);
    }

}
