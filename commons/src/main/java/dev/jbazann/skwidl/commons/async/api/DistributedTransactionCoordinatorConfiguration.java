package dev.jbazann.skwidl.commons.async.api;

import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.EventAnswerPublisher;
import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitCoordinatorListenerService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionCoordinatorService;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransactionRepository;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.identity.IdentityConfiguration;
import dev.jbazann.skwidl.commons.shared.storage.RedisConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        EventsConfiguration.class,
        IdentityConfiguration.class,
        RedisConfiguration.class,
})
public class DistributedTransactionCoordinatorConfiguration {

    @Bean
    public TransactionCoordinatorService standardTransactionCoordinatorService(ApplicationMember member,
                                                                               CoordinatedTransactionRepository repository,
                                                                               EventAnswerPublisher publisher,
                                                                               DomainEventBuilder builder) {
        return new TransactionCoordinatorService(member, repository, publisher, builder);
    }

    @Bean
    public RabbitCoordinatorListenerService standardRabbitCoordinatorListenerService(
            TransactionCoordinatorService coordinator) {
        return new RabbitCoordinatorListenerService(coordinator);
    }

}
