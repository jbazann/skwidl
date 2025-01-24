package dev.jbazann.skwidl.commons.async.api;

import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitListenerService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionCoordinatorService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@Import({
        EventsConfiguration.class
})
public class DistributedTransactionListenerConfiguration {

    @Bean
    public RabbitListenerService standardDomainEventProcessor(
            @Lazy @Autowired(required = false) TransactionCoordinatorService coordinator,
            @Lazy @Autowired(required = false) TransactionMemberService member
    ) {
        return new RabbitListenerService(coordinator, member);
    }

}
