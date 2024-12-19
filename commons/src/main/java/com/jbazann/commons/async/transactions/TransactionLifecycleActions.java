package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.transactions.data.Transaction;
import com.jbazann.commons.async.transactions.data.TransactionRepository;
import com.jbazann.commons.async.transactions.data.TransientTransaction;

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

    protected Transaction fetchOrCreateFor(DomainEvent event) {
        Transaction transaction = repository.findById(event.transaction().id());
        if (transaction == null) {
            transaction = new TransientTransaction().from(event);
            transaction.status(Transaction.TransactionStatus.UNKNOWN);
            repository.save(transaction);
        }
        return transaction;
    }





}
