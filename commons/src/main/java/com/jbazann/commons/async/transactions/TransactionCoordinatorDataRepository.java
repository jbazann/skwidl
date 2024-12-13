package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.events.DomainEvent;

public interface TransactionCoordinatorDataRepository {

    TransactionCoordinatorData getForEvent(DomainEvent event);
    TransactionCoordinatorData persist(TransactionCoordinatorData transaction);

}
