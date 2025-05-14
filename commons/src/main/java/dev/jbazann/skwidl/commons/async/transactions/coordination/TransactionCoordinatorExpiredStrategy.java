package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorExpiredStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    protected TransactionCoordinatorExpiredStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_EXPIRED);
        DomainEvent response = builder.answer(event)
                .withType(DomainEvent.Type.REJECT)
                .withContext("Transactional operation timed out.")
                .asDomainEvent();
        return new TransactionCoordinatorStrategyResult(Optional.of(response), transaction);
    }

}
