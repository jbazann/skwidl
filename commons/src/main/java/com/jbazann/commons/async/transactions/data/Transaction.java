package com.jbazann.commons.async.transactions.data;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Transaction {

    // Getters
    UUID id();
    LocalDateTime expires();
    TransientTransaction.TransactionStatus status();

    // Setters
    UUID id(UUID id);
    LocalDateTime expires(LocalDateTime expires);
    TransientTransaction.TransactionStatus status(TransientTransaction.TransactionStatus status);

}
