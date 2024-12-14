package com.jbazann.commons.async.transactions.data;

import java.util.UUID;

public interface CoordinatedTransactionRepository {

    CoordinatedTransaction findById(UUID transactionId);
    CoordinatedTransaction persist(CoordinatedTransaction transaction);

}
