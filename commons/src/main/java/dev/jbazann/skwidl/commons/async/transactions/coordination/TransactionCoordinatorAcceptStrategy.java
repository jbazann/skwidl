package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorAcceptStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    protected TransactionCoordinatorAcceptStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        transaction.addAccept(event.sentBy());

        DomainEvent response = null;
        if (transaction.isFullyAccepted()) {
            transaction.status(CoordinatedTransaction.TransactionStatus.COMMITTED);
            response = builder.answer(event)
                    .withType(DomainEvent.Type.COMMIT)
                    .withContext("Accepted by full quorum.")
                    .asDomainEvent();
        }

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
