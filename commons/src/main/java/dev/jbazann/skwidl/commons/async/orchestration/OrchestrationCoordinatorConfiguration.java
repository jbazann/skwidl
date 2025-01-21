package dev.jbazann.skwidl.commons.async.orchestration;

import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.EventAnswerPublisher;
import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransactionRepository;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        EventsConfiguration.class
})
public class OrchestrationCoordinatorConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CoordinatedTransactionRepository missingCoordinatorDataRepository() {
        throw new IllegalStateException("A CoordinatedTransactionRepository @Bean was not provided.");
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCoordinatorService standardTransactionCoordinatorService(ApplicationMember member,
                                                                               CoordinatedTransactionRepository repository,
                                                                               EventAnswerPublisher publisher,
                                                                               DomainEventBuilder builder) {
        return new TransactionCoordinatorService(member, repository, publisher, builder);
    }

}
