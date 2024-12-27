package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.EventAnswerPublisher;
import com.jbazann.commons.async.events.EventsConfiguration;
import com.jbazann.commons.async.transactions.TransactionsConfiguration;
import com.jbazann.commons.async.transactions.api.TransactionLifecycleActions;
import com.jbazann.commons.async.transactions.TransactionPhaseExecutor;
import com.jbazann.commons.async.transactions.TransactionPhaseRegistrar;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TransactionsConfiguration.class,
        EventsConfiguration.class
})
public class OrchestrationMemberConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TransactionResponseProvider standardTransactionResponseProvider(EventAnswerPublisher publisher) {
        return new TransactionResponseProvider(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    TransactionPhaseRegistrar standardTransactionPhaseRegistrar(ApplicationContext context) {
        return new TransactionPhaseRegistrar(context);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionPhaseExecutor standardTransactionPhaseExecutor(TransactionPhaseRegistrar registrar, TransactionLifecycleActions actions) {
        return new TransactionPhaseExecutor(registrar, actions);
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainEventProcessorService standardDomainEventProcessor(ApplicationMember identity,
                                                                    TransactionPhaseExecutor executor,
                                                                    TransactionResponseProvider responseProvider,
                                                                    EventAnswerPublisher publisher) {
        return new DomainEventProcessorService(executor, identity, responseProvider, publisher);
    }

}
