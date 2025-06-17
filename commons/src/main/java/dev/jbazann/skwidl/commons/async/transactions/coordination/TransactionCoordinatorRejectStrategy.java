package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorRejectStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilderFactory events;

    public TransactionCoordinatorRejectStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilderFactory events) {
        this.transaction = transaction;
        this.event = event;
        this.events = events;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        transaction.addReject(event.sentBy());
        final boolean isFirstRejection = !transaction.isRejected();
        transaction.status(transaction.isFullyRejected() ?
                CoordinatedTransaction.TransactionStatus.CONCLUDED_REJECT :
                CoordinatedTransaction.TransactionStatus.REJECTED
        );

        DomainEvent response = null;
        if (isFirstRejection) {
            response = events.create(event.getClass())
                    .answer(event)
                    .setType(DomainEvent.Type.REJECT)
                    .setContext("Transaction rejected by %s.", event.sentBy())
                    .build();
        } else if (transaction.isFullyRejected()) {
            response = events.create(event.getClass())
                    .answer(event)
                    .setType(DomainEvent.Type.ACK)
                    .setContext("Transaction concluded by rejection.")
                    .build();
        }

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
