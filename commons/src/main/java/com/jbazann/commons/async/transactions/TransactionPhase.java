package com.jbazann.commons.async.transactions;


import com.jbazann.commons.async.events.DomainEvent;

public interface TransactionPhase {

    Class<? extends DomainEvent> getEventClass();
    TransactionResult runForEvent(DomainEvent event);

}
