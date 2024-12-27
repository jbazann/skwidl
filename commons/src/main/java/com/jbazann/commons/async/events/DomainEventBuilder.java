package com.jbazann.commons.async.events;

import com.jbazann.commons.async.events.specialized.CancelAcceptedOrderEvent;
import com.jbazann.commons.async.events.specialized.CancelPreparedOrderEvent;
import com.jbazann.commons.async.events.specialized.DeliverOrderEvent;
import com.jbazann.commons.async.events.specialized.DiscardedEvent;
import com.jbazann.commons.async.transactions.TransactionQuorum;
import com.jbazann.commons.async.transactions.api.implement.Transaction;
import com.jbazann.commons.identity.ApplicationMember;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DomainEventBuilder {

    private DomainEvent event;
    private final ApplicationMember thisApplication;

    public DomainEventBuilder(ApplicationMember identity) {
        this.thisApplication = identity;
    }

    /**
     * Instantiates and returns a new {@link DomainEventBuilder} intended
     * for the creation of a single event.
     */
    public DomainEventBuilder create() {
        final DomainEventBuilder builder = new DomainEventBuilder(thisApplication);
        builder.event = DomainEvent.init(new GenericDomainEvent());
        event.transaction().quorum().coordinator(thisApplication);
        event.transaction().quorum().members(List.of(thisApplication));
        return builder;
    }

    /**
     * Instantiates and returns a new {@link DomainEventBuilder} intended
     * for the creation of a single event in response to another well-formed
     * event of the same concrete type.
     */
    public DomainEventBuilder answer(DomainEvent event) {
        final DomainEventBuilder builder = create();
        builder.event = DomainEvent.copyOf(event).sentBy(thisApplication);
        return builder;
    }

    /**
     * Instantiates and returns a new {@link DomainEventBuilder} intended
     * for completing the creation of an already initialized event.
     */
    public DomainEventBuilder forEvent(DomainEvent event) {
        final DomainEventBuilder builder = create();
        builder.event = event;
        return builder;
    }

    public DomainEventBuilder withType(DomainEvent.Type type) {
        event.type(type);
        return this;
    }

    public DomainEventBuilder withType(DomainEvent.Type type, String context) {
        event.type(type).context(context);
        return this;
    }

    public DomainEventBuilder withContext(String context) {
        event.context(context);
        return this;
    }

    public DomainEventBuilder withQuorumMembers(List<ApplicationMember> quorum) {
        event.transaction().quorum().members(quorum);
        return this;
    }

    public DomainEventBuilder withCoordinator(ApplicationMember coordinator) {
        event.transaction().quorum().coordinator(coordinator);
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

    public DomainEvent asDomainEvent() {
        return event;
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

    public DomainEvent discard() {
        return DiscardedEvent.discard(event);
    }

    public DomainEvent discard(String context) {
        return DiscardedEvent.discard(event, context);
    }
}
