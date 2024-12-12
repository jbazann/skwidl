package com.jbazann.orders.commons.async.transactions;

import java.util.UUID;

public interface TransactionCoordinatorDataRepository {

    TransactionCoordinatorData findByIdOrCreate(UUID transactionId);
    TransactionCoordinatorData persist(TransactionCoordinatorData transaction);

}
