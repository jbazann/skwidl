package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface TransactionStage {

    default @NotNull List<@NotNull @Valid EntityLock> getRequiredLocks(@NotNull @Valid DomainEvent domainEvent) {
        return List.of();
    }

    @NotNull @Valid TransactionResult runStage(@NotNull @Valid DomainEvent domainEvent,
                               @NotNull @Valid Transaction transaction);

}
