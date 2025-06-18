package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorExpiredStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilderFactory events;

    public TransactionCoordinatorExpiredStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilderFactory events) {
        this.transaction = transaction;
        this.event = event;
        this.events = events;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        DomainEvent response = null;
        if (!transaction.isExpired()) { // First expired event.
            transaction.status(CoordinatedTransaction.TransactionStatus.EXPIRED);
            response = events.create(event.getClass())
                    .answer(event)
                    .setType(DomainEvent.Type.REJECT)
                    .setContext("Transactional operation timed out.")
                    .build();
        } else {
            switch (event.type()) {
                case REJECT -> transaction.addReject(event.sentBy());
                case ROLLBACK -> transaction.addRollback(event.sentBy());
            }

            if (transaction.isFullyRejected()) {
                transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_EXPIRED);
                response = events.create(event.getClass())
                        .answer(event)
                        .setType(DomainEvent.Type.ACK)
                        .setContext("Transaction concluded by expiration.")
                        .build();
            }
        }

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
