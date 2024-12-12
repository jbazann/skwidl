package com.jbazann.orders.commons.async.transactions;


import com.jbazann.orders.commons.async.events.DomainEvent;

public interface TransactionPhase {

    Class<? extends DomainEvent> getEventClass();
    TransactionResult runForEvent(DomainEvent event);

}
