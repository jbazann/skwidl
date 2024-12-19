package com.jbazann.commons.async.transactions.data;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Transaction {

    // Getters
    UUID id();
    LocalDateTime expires();
    TransactionStatus status();

    // Setters
    UUID id(UUID id);
    LocalDateTime expires(LocalDateTime expires);
    TransactionStatus status(TransactionStatus status);

    enum TransactionStatus {
        UNKNOWN,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        ROLLED_BACK,
        ERROR,
    }

}
