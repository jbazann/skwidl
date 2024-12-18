package com.jbazann.commons.async.transactions.data;

import java.util.UUID;

public interface TransactionRepository {

    Transaction findById(UUID transactionId);
    Transaction save(Transaction transaction);

}
