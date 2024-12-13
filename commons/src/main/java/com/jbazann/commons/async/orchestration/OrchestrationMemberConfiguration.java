package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEventTracer;
import com.jbazann.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.commons.async.transactions.TransactionCoordinatorDataRepository;
import com.jbazann.commons.async.transactions.TransactionPhaseExecutor;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrchestrationMemberConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationMember missingIdentity() {
        throw new IllegalStateException("An ApplicationMember @Bean was not provided.");
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCoordinatorDataRepository missingCoordinatorDataRepository() {
        throw new IllegalStateException("A TransactionCoordinatorDataRepository @Bean was not provided.");
    }// TODO this should be optional.

    @Bean
    public DomainEventTracer standardDomainEventTracer(ApplicationMember identity, RabbitPublisher publisher) {
        return new DomainEventTracer(identity, publisher);
    }

    @Bean
    public TransactionResponseProvider standardTransactionResponseProvider(DomainEventTracer tracer) {
        return new TransactionResponseProvider(tracer);
    }

    @Bean
    public DomainEventProcessorService standardDomainEventProcessor(ApplicationMember identity,
                                                                    TransactionPhaseExecutor executor,
                                                                    TransactionResponseProvider responseProvider,
                                                                    DomainEventTracer tracer) {
        return new DomainEventProcessorService(executor, identity, responseProvider, tracer);
    }

    @Bean
    public TransactionCoordinatorService standardTransactionCoordinatorService(ApplicationMember member,
                                                                               TransactionCoordinatorDataRepository repository,
                                                                               DomainEventTracer tracer) {
        return new TransactionCoordinatorService(member, repository, tracer);
    }

}
