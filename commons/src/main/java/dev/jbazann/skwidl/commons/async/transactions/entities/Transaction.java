package dev.jbazann.skwidl.commons.async.transactions.entities;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionQuorum;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
public class Transaction {

    protected UUID id;
    protected LocalDateTime expires;
    protected TransactionStatus status;
    protected TransactionQuorum quorum;

    public enum TransactionStatus {
        UNKNOWN,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        ROLLED_BACK,
        ERROR,
    }

    public static Transaction from(DomainEvent event) {
        return new Transaction()
                .id(event.transaction().id())
                .expires(event.transaction().expires())
                .status(event.transaction().status());
    }

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }


}
