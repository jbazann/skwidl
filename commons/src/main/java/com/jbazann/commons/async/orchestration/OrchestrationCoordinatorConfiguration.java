package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEventBuilder;
import com.jbazann.commons.async.events.EventAnswerPublisher;
import com.jbazann.commons.async.events.EventsConfiguration;
import com.jbazann.commons.async.transactions.api.implement.CoordinatedTransactionRepository;
import com.jbazann.commons.identity.ApplicationMember;
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
