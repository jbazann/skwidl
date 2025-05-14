package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorRejectStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    protected TransactionCoordinatorRejectStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
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
            response = builder.answer(event)
                    .withType(DomainEvent.Type.REJECT)
                    .withContext("Transaction rejected by %s.", event.sentBy())
                    .asDomainEvent();
        } else if (transaction.isFullyRejected()) {
            response = builder.answer(event)
                    .withType(DomainEvent.Type.ACK)
                    .withContext("Transaction concluded by rejection.")
                    .asDomainEvent();
        }

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
