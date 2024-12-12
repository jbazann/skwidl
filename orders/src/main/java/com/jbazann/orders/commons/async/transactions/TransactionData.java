package com.jbazann.orders.commons.async.transactions;

import com.jbazann.orders.commons.identity.ApplicationMember;
import com.jbazann.orders.commons.utils.TimeProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public abstract class TransactionData {

    private UUID id;
    private LocalDateTime expires;
    private TransactionStatus status;
    private TransactionQuorum transactionQuorum;

    public boolean expired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

    public enum TransactionStatus {
        UNKNOWN,
        STARTED,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        ROLLED_BACK,
    }

}
