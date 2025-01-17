package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.identity.IdentityConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        RabbitConfiguration.class,
        IdentityConfiguration.class
})
public class EventsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DomainEventBuilder domainEventBuilder(ApplicationMember identity) {
        return new DomainEventBuilder(identity);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventAnswerPublisher eventAnswerPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        return new EventAnswerPublisher(publisher, builder);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventRequestPublisher eventRequestPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        return new EventRequestPublisher(publisher, builder);
    }

}
