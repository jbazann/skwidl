package dev.jbazann.skwidl.commons.async.api;

import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitMemberListenerService;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.async.transactions.TransactionMemberService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResponseService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionStageExecutorService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionsConfiguration;
import dev.jbazann.skwidl.commons.identity.ApplicationMemberRegistry;
import dev.jbazann.skwidl.commons.identity.IdentityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TransactionsConfiguration.class,
        EventsConfiguration.class,
        IdentityConfiguration.class,
})
public class DistributedTransactionMemberConfiguration {

    @Bean
    public TransactionResponseService standardTransactionResponseService(
            RabbitPublisher publisher,
            DomainEventBuilderFactory factory
    ) {
        return new TransactionResponseService(publisher, factory);
    }

    @Bean
    public TransactionMemberService standardTransactionMemberService(
            TransactionStageExecutorService executor,
            ApplicationMemberRegistry memberRegistry,
            TransactionResponseService response,
            RabbitPublisher publisher,
            DomainEventBuilderFactory factory
    ) {
        return new TransactionMemberService(executor, memberRegistry.SELF, response, publisher, factory);
    }

    @Bean
    public RabbitMemberListenerService standardDomainEventProcessor(
            TransactionMemberService member) {
        return new RabbitMemberListenerService(member);
    }

}
