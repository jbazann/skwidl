package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;

import java.util.Optional;

public class TransactionCoordinatorLogStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    private final Logger log = LoggerFactory.get(getClass());

    public TransactionCoordinatorLogStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        log.debug("LogStrategy selected for TRANSACTION %s – EVENT %s", transaction, event);
        DomainEvent response = builder.answer(event)
                .withType(DomainEvent.Type.ERROR)
                .withContext("LogStrategy selected for TRANSACTION %s – EVENT %s", transaction, event)
                .asDomainEvent();

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
