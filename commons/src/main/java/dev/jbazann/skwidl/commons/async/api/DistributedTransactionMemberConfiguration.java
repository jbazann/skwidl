package dev.jbazann.skwidl.commons.async.api;

import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitMemberListenerService;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.async.transactions.*;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
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
            ApplicationMember member,
            TransactionResponseService response,
            RabbitPublisher publisher,
            DomainEventBuilderFactory factory
    ) {
        return new TransactionMemberService(executor, member, response, publisher, factory);
    }

    @Bean
    public RabbitMemberListenerService standardDomainEventProcessor(
            TransactionMemberService member) {
        return new RabbitMemberListenerService(member);
    }

}
