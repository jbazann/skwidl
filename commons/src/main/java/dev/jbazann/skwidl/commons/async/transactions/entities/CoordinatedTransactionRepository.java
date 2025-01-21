package dev.jbazann.skwidl.commons.async.transactions.entities;

import java.util.UUID;

public interface CoordinatedTransactionRepository {

    CoordinatedTransaction findById(UUID transactionId);
    CoordinatedTransaction save(CoordinatedTransaction transaction);

}
