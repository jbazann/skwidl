package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorRollbackStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    public TransactionCoordinatorRollbackStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        transaction.addRollback(event.sentBy());
        transaction.status(transaction.isFullyRejected() ?
                CoordinatedTransaction.TransactionStatus.CONCLUDED_REJECT :
                CoordinatedTransaction.TransactionStatus.REJECTED
        );

        DomainEvent response = !transaction.isFullyRejected() ? null :
                builder.answer(event)
                        .withType(DomainEvent.Type.ACK)
                        .withContext("Transaction concluded by rollback.")
                        .asDomainEvent();

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
