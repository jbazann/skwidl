package com.jbazann.commons.async.transactions;


import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.transactions.data.Transaction;

public interface TransactionPhase {

    Class<? extends DomainEvent> getEventClass();
    TransactionResult runForEvent(DomainEvent event, Transaction transaction);

}
