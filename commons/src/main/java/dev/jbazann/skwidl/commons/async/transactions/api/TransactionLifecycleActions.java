package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionRepository;

public class TransactionLifecycleActions {

    private final TransactionRepository repository;

    public TransactionLifecycleActions(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction error(Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ERROR);
        return repository.save(transaction);
    }

    public Transaction reject(Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.REJECTED);
        return repository.save(transaction);
    }

    public Transaction accept(Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ACCEPTED);
        return repository.save(transaction);
    }

    public Transaction rollback(Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.ROLLED_BACK);
        return repository.save(transaction);
    }

    public Transaction commit(Transaction transaction) {
        transaction.status(Transaction.TransactionStatus.COMMITTED);
        return repository.save(transaction);
    }

    public Transaction fetchOrCreateForEvent(DomainEvent event) {
        Transaction transaction = repository.findById(event.transaction().id()).orElse(null);
        if (transaction == null) {
            transaction = Transaction.from(event);
            transaction.status(Transaction.TransactionStatus.UNKNOWN);
            repository.save(transaction);
        }
        return transaction;
    }





}
