package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.utils.TimeProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public abstract class TransactionData {

    private UUID id;
    private LocalDateTime expires;
    private TransactionStatus status;

    public TransactionData initFrom(final TransactionData data, DomainEvent event) {
        return data.id(event.transaction().id())
                .expires(event.transaction().expires())
                .status(TransactionStatus.NOT_PERSISTED);
    }

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

    public enum TransactionStatus {
        NOT_PERSISTED,
        STARTED,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        ROLLED_BACK,
    }

}
