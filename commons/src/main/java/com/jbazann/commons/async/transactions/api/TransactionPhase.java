package com.jbazann.commons.async.transactions.api;


import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.transactions.TransactionResult;
import com.jbazann.commons.async.transactions.api.implement.Transaction;

public interface TransactionPhase {

    Class<? extends DomainEvent> getEventClass();
    TransactionResult runForEvent(DomainEvent domainEvent, Transaction transaction);

}
