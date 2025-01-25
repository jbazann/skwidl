package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class TransactionLifecycleActions {

    private final TransactionRepository repository;

    public TransactionLifecycleActions(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction error(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ERROR);
        return repository.save(transaction);
    }

    public Transaction reject(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.REJECTED);
        return repository.save(transaction);
    }

    public Transaction accept(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ACCEPTED);
        return repository.save(transaction);
    }

    public Transaction rollback(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ROLLED_BACK);
        return repository.save(transaction);
    }

    public Transaction commit(@NotNull @Valid Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.COMMITTED);
        return repository.save(transaction);
    }

    public Transaction fetchOrCreateForEvent(@NotNull @Valid DomainEvent event) {
        Transaction transaction = repository.findById(event.transaction().id()).orElse(null);
        if (transaction == null) {
            transaction = Transaction.from(event);
            transaction.status(Transaction.TransactionStatus.UNKNOWN);
            repository.save(transaction);
        }
        return transaction;
    }





}
