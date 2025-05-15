package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorExpiredStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    public TransactionCoordinatorExpiredStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        DomainEvent response = null;
        if (!transaction.isExpired()) { // First expired event.
            transaction.status(CoordinatedTransaction.TransactionStatus.EXPIRED);
            response = builder.answer(event)
                    .withType(DomainEvent.Type.REJECT)
                    .withContext("Transactional operation timed out.")
                    .asDomainEvent();
        } else {
            switch (event.type()) {
                case REJECT -> transaction.addReject(event.sentBy());
                case ROLLBACK -> transaction.addRollback(event.sentBy());
            }

            if (transaction.isFullyRejected()) {
                transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_EXPIRED);
                response = builder.answer(event)
                        .withType(DomainEvent.Type.ACK)
                        .withContext("Transaction concluded by expiration.")
                        .asDomainEvent();
            }
        }

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
