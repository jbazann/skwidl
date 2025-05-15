package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.coordination.*;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

public class TransactionCoordinatorStrategySelector {

    private final DomainEventBuilder builder;

    public TransactionCoordinatorStrategySelector(DomainEventBuilder builder) {
        this.builder = builder;
    }

    public TransactionCoordinatorStrategy getStrategy(DomainEvent event, CoordinatedTransaction transaction) {
        if (transaction.isCommitted()) {
            return new TransactionCoordinatorCommitStrategy(transaction, event, builder);
        }

        if (transaction.isTimeExpired() && !transaction.isCommitted()) { // redundant for clarity.
            return new TransactionCoordinatorExpiredStrategy(transaction, event, builder);
        }

        return switch (event.type()) {
            case ACCEPT -> new TransactionCoordinatorAcceptStrategy(transaction, event, builder);
            case REJECT -> new TransactionCoordinatorRejectStrategy(transaction, event, builder);
            case ROLLBACK -> new TransactionCoordinatorRollbackStrategy(transaction, event, builder);
            default -> new TransactionCoordinatorLogStrategy(transaction, event, builder);
        };
    }

}
