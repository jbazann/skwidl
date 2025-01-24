package dev.jbazann.skwidl.commons.async.transactions.api;


import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;

public interface TransactionPhase {

    TransactionResult runForEvent(DomainEvent domainEvent, Transaction transaction);

}
