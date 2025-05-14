package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public record TransactionCoordinatorStrategyResult(
        Optional<DomainEvent> response,
        CoordinatedTransaction transaction
) {
}
