package dev.jbazann.skwidl.commons.async.transactions.api;


import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.implement.Transaction;

public interface TransactionPhase {

    Class<? extends DomainEvent> getEventClass();
    TransactionResult runForEvent(DomainEvent domainEvent, Transaction transaction);

}
