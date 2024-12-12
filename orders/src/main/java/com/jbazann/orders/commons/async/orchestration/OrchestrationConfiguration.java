package com.jbazann.orders.commons.async.orchestration;

import com.jbazann.orders.commons.async.events.DomainEventTracer;
import com.jbazann.orders.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.orders.commons.async.transactions.TransactionPhaseExecutor;
import com.jbazann.orders.commons.identity.ApplicationMember;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrchestrationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationMember missingIdentity() {
        throw new IllegalStateException("An ApplicationMember Bean was not provided.");
    }

    @Bean
    public DomainEventTracer standardDomainEventTracer(ApplicationMember identity) {
        return new DomainEventTracer(identity);
    }

    @Bean
    public TransactionResponseProvider standardTransactionResponseProvider(DomainEventTracer tracer) {
        return new TransactionResponseProvider(tracer);
    }

    @Bean
    public DomainEventProcessorService standardDomainEventProcessor(RabbitPublisher publisher,
                                                                    ApplicationMember identity,
                                                                    TransactionPhaseExecutor executor,
                                                                    TransactionResponseProvider responseProvider,
                                                                    DomainEventTracer tracer) {
        return new DomainEventProcessorService(publisher, executor, identity, responseProvider, tracer);
    }

}
