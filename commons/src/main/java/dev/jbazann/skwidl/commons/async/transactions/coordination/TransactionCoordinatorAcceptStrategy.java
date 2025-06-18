package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

import static dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction.TransactionStatus.STARTED;

public class TransactionCoordinatorAcceptStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilderFactory events;

    public TransactionCoordinatorAcceptStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilderFactory events) {
        this.transaction = transaction;
        this.event = event;
        this.events = events;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        if (STARTED.equals(transaction.status())) transaction.addAccept(event.sentBy());

        DomainEvent response = null;
        if (transaction.isFullyAccepted()) {
            transaction.status(CoordinatedTransaction.TransactionStatus.COMMITTED);
            response = events.create(event.getClass())
                    .answer(event)
                    .setType(DomainEvent.Type.COMMIT)
                    .setContext("Accepted by full quorum.")
                    .build();
        }

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
