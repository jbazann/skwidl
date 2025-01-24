package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;

import java.util.List;

public interface TransactionStage {

    default List<EntityLock> getRequiredLocks(DomainEvent domainEvent) {
        throw new UnsupportedOperationException("Unimplemented method.");
    }

    TransactionResult runStage(DomainEvent domainEvent, Transaction transaction);

}
