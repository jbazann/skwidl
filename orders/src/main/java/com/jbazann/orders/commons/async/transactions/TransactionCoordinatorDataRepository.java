package com.jbazann.orders.commons.async.transactions;

import com.jbazann.orders.commons.async.events.DomainEvent;

public interface TransactionCoordinatorDataRepository {

    TransactionCoordinatorData getForEvent(DomainEvent event);
    TransactionCoordinatorData persist(TransactionCoordinatorData transaction);

}
