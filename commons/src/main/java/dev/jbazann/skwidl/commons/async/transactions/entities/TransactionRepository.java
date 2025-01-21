package dev.jbazann.skwidl.commons.async.transactions.entities;

import java.util.UUID;

public interface TransactionRepository {

    Transaction findById(UUID transactionId);
    Transaction save(Transaction transaction);

}
