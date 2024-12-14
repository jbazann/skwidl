package com.jbazann.orders.order.commons.async.transactions.data;

import com.jbazann.commons.async.transactions.data.Transaction;
import com.jbazann.commons.async.transactions.data.TransientTransaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode
@Accessors(chain = true, fluent = true)
@Document(collection="transaction")
public class PersistentTransaction implements Transaction {

    private UUID id;
    private LocalDateTime expires;
    private TransientTransaction.TransactionStatus status;

}
