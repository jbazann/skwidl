package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.transactions.coordination.*;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

public class TransactionCoordinatorStrategySelector {

    private final DomainEventBuilderFactory factory;

    public TransactionCoordinatorStrategySelector(DomainEventBuilderFactory factory) {
        this.factory = factory;
    }

    public TransactionCoordinatorStrategy getStrategy(DomainEvent event, CoordinatedTransaction transaction) {
        if (transaction.isCommitted()) {
            return new TransactionCoordinatorCommitStrategy(transaction, event, factory);
        }

        if (transaction.isTimeExpired() && !transaction.isCommitted()) { // redundant for clarity.
            return new TransactionCoordinatorExpiredStrategy(transaction, event, factory);
        }

        return switch (event.type()) {
            case ACCEPT -> new TransactionCoordinatorAcceptStrategy(transaction, event, factory);
            case REJECT -> new TransactionCoordinatorRejectStrategy(transaction, event, factory);
            case ROLLBACK -> new TransactionCoordinatorRollbackStrategy(transaction, event, factory);
            default -> new TransactionCoordinatorLogStrategy(transaction, event, factory);
        };
    }

}
