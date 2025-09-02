package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;

import java.util.Optional;

public class TransactionCoordinatorLogStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilderFactory events;

    private final Logger log = LoggerFactory.get(getClass());

    public TransactionCoordinatorLogStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilderFactory events) {
        this.transaction = transaction;
        this.event = event;
        this.events = events;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        log.debug("LogStrategy selected for TRANSACTION {} – EVENT {}", transaction, event);
        DomainEvent response = events.create(event.getClass())
                .answer(event)
                .setType(DomainEvent.Type.ERROR)
                .setContext("LogStrategy selected for TRANSACTION %s – EVENT %s", transaction, event)
                .build();

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
