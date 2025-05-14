package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.coordination.TransactionCoordinatorStrategy;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

public class TransactionCoordinatorStrategySelector {

    public TransactionCoordinatorStrategy getStrategy(DomainEvent event, CoordinatedTransaction transaction) {
        // TODO tomorrow 8)
    }

}
