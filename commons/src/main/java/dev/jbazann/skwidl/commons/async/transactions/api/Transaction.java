package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Transaction {

    UUID id();
    LocalDateTime expires();
    TransactionStatus status();
    TransactionQuorum quorum();

    Transaction id(UUID id);
    Transaction expires(LocalDateTime expires);
    Transaction status(TransactionStatus status);
    Transaction quorum(TransactionQuorum quorum);

    boolean isExpired();

    enum TransactionStatus {
        UNKNOWN,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        ROLLED_BACK,
        ERROR,
    }

    default @NotNull @Valid Transaction initFromEvent(@NotNull @Valid DomainEvent event) {
        return this.id(event.transaction().id())
                .expires(event.transaction().expires())
                .quorum(event.transaction().quorum())
                .status(Transaction.TransactionStatus.UNKNOWN);
    }

}
