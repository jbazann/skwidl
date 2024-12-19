package com.jbazann.commons.async.events;

import com.jbazann.commons.async.events.specialized.CancelAcceptedOrderEvent;
import com.jbazann.commons.async.events.specialized.CancelPreparedOrderEvent;
import com.jbazann.commons.async.events.specialized.DeliverOrderEvent;
import com.jbazann.commons.async.events.specialized.DiscardedEvent;
import com.jbazann.commons.async.transactions.TransactionQuorum;
import com.jbazann.commons.async.transactions.data.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public final class DomainEventBuilder {

    private DomainEvent event = DomainEvent.init(new GenericDomainEvent());

    public DomainEventBuilder create() {
        this.event = DomainEvent.init(new GenericDomainEvent());
        return this;
    }

    public DomainEventBuilder withTransaction(DomainEvent.TransactionDTO transaction) {
        event.transaction(transaction);
        return this;
    }

    public DomainEventBuilder withTransaction(
            UUID id,
            TransactionQuorum quorum,
            Transaction.TransactionStatus status,
            LocalDateTime expires
    ) {
        event.transaction(new DomainEvent.TransactionDTO()
                .id(id)
                .quorum(quorum)
                .status(status)
                .expires(expires)
        );
        return this;
    }

    public CancelAcceptedOrderEvent asCancelAcceptedOrderEvent(
            UUID orderId,
            UUID customerId,
            BigDecimal returnedFunds
    ) {
        return (CancelAcceptedOrderEvent) DomainEvent.init(
                new CancelAcceptedOrderEvent()
                        .orderId(orderId)
                        .customerId(customerId)
                        .returnedFunds(returnedFunds)
        );
    }

    public CancelPreparedOrderEvent asCancelPreparedOrderEvent(
            UUID orderId,
            UUID customerId,
            BigDecimal returnedFunds,
            Map<UUID, Integer> returnedStock
    ) {
        return (CancelPreparedOrderEvent) DomainEvent.init(
                new CancelPreparedOrderEvent()
                        .orderId(orderId)
                        .customerId(customerId)
                        .returnedFunds(returnedFunds)
                        .returnedStock(returnedStock)
        );
    }

    public DeliverOrderEvent asDeliverOrderEvent(
            UUID orderId,
            UUID customerId,
            BigDecimal returnedFunds
    ) {
        return (DeliverOrderEvent) DomainEvent.init(
                new DeliverOrderEvent()
                        .orderId(orderId)
                        .customerId(customerId)
                        .returnedFunds(returnedFunds)
        );
    }

    /**
     * Wrapper for consistency. See {@link DiscardedEvent#discard(DomainEvent)}
     */
    public DiscardedEvent discard(DomainEvent event) {
        return DiscardedEvent.discard(event);
    }

}
