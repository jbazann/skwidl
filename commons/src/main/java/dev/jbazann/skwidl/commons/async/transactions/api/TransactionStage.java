package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface TransactionStage {

    default List<EntityLock> getRequiredLocks(@NotNull @Valid DomainEvent domainEvent) {
        throw new UnsupportedOperationException("Unimplemented method.");
    }

    TransactionResult runStage(@NotNull @Valid DomainEvent domainEvent,
                               @NotNull @Valid Transaction transaction);

}
