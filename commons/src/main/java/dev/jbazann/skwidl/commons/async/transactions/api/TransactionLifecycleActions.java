package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionRepository;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class TransactionLifecycleActions {

    private final TransactionRepository repository;
    private final Logger log = LoggerFactory.get(TransactionLifecycleActions.class);

    public TransactionLifecycleActions(TransactionRepository repository) {
        this.repository = repository;
    }

    public @NotNull @Valid Transaction error(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ERROR);
        return log.result(repository.save(transaction));
    }

    public @NotNull @Valid Transaction reject(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.REJECTED);
        return log.result(repository.save(transaction));
    }

    public @NotNull @Valid Transaction accept(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ACCEPTED);
        return log.result(repository.save(transaction));
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull @Valid Transaction rollback(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ROLLED_BACK);
        return log.result(repository.save(transaction));
    }

    public @NotNull @Valid Transaction commit(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.COMMITTED);
        return log.result(repository.save(transaction));
    }

    public @NotNull @Valid Transaction fetchOrCreateForEvent(@NotNull @Valid DomainEvent event) {
        log.method(event);
        return log.result(repository.findById(event.transaction().id()).orElseGet(() -> {
            log.debug("Transaction {} not found. Initializing and saving...", event.transaction().id());
            return repository.save(Transaction.from(event));
        }));
    }

}
