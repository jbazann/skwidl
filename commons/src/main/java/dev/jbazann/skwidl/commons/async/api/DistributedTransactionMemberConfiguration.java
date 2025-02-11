package dev.jbazann.skwidl.commons.async.api;

import dev.jbazann.skwidl.commons.async.events.EventAnswerPublisher;
import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.transactions.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TransactionsConfiguration.class,
        EventsConfiguration.class,
        DistributedTransactionListenerConfiguration.class
})
public class DistributedTransactionMemberConfiguration {

    @Bean
    public TransactionResponseService standardTransactionResponseProvider(EventAnswerPublisher publisher) {
        return new TransactionResponseService(publisher);
    }

}
