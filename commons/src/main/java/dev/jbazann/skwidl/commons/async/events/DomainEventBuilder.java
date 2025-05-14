package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.events.specialized.CancelAcceptedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DiscardedEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionQuorum;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
public class DomainEventBuilder {

    private @NotNull @Valid DomainEvent event;
    private final @NotNull ApplicationMember thisApplication;

    public DomainEventBuilder(ApplicationMember identity) {
        this.thisApplication = identity;
    }

    /**
     * Instantiates and returns a new {@link DomainEventBuilder} intended
     * for the creation of a single event.
     */
    public @NotNull DomainEventBuilder create() {
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
    public @NotNull @Valid DomainEventBuilder answer(@NotNull @Valid DomainEvent event) {
        final DomainEventBuilder builder = create();
        builder.event = DomainEvent.copyOf(event).sentBy(thisApplication);
        return builder;
    }

    /**
     * Instantiates and returns a new {@link DomainEventBuilder} intended
     * for completing the creation of an already initialized event.
     */
    public @NotNull DomainEventBuilder forEvent(@NotNull DomainEvent event) {
        final DomainEventBuilder builder = create();
        builder.event = event;
        return builder;
    }

    public @NotNull DomainEventBuilder withType(@NotNull DomainEvent.Type type) {
        event.type(type);
        return this;
    }

    public @NotNull DomainEventBuilder withType(@NotNull DomainEvent.Type type, @NotNull String context) {
        event.type(type).context(context);
        return this;
    }

    public @NotNull DomainEventBuilder withContext(@NotNull String context) {
        event.context(context);
        return this;
    }

    public @NotNull DomainEventBuilder withContext(@NotNull String format, @NotNull Object... args) {
        event.context(String.format(format,args));
        return this;
    }

    public @NotNull DomainEventBuilder withQuorumMembers(@NotNull @NotEmpty List<@NotNull ApplicationMember> quorum) {
        event.transaction().quorum().members(quorum);
        return this;
    }

    public @NotNull DomainEventBuilder withCoordinator(@NotNull ApplicationMember coordinator) {
        event.transaction().quorum().coordinator(coordinator);
        return this;
    }

    public @NotNull DomainEventBuilder withTransaction(@NotNull @Valid Transaction transaction) {
        event.transaction(transaction);
        return this;
    }

    public @NotNull DomainEventBuilder withTransaction(
            @NotNull UUID id,
            @NotNull @Valid TransactionQuorum quorum,
            @NotNull Transaction.TransactionStatus status,
            @NotNull LocalDateTime expires
    ) {
        event.transaction(new Transaction()
                .id(id)
                .quorum(quorum)
                .status(status)
                .expires(expires)
        );
        return this;
    }

    public @NotNull DomainEvent asDomainEvent() {
        return event;
    }

    public @NotNull CancelAcceptedOrderEvent asCancelAcceptedOrderEvent(
            @NotNull UUID orderId,
            @NotNull UUID customerId,
            @NotNull @Min(0) BigDecimal returnedFunds
    ) {
        return (CancelAcceptedOrderEvent) DomainEvent.init(
                new CancelAcceptedOrderEvent()
                        .orderId(orderId)
                        .customerId(customerId)
                        .returnedFunds(returnedFunds)
        );
    }

    public @NotNull CancelPreparedOrderEvent asCancelPreparedOrderEvent(
            @NotNull UUID orderId,
            @NotNull UUID customerId,
            @NotNull @Min(0) BigDecimal returnedFunds,
            @NotNull @NotEmpty Map<@NotNull UUID,@NotNull Integer> returnedStock
    ) {
        return (CancelPreparedOrderEvent) DomainEvent.init(
                new CancelPreparedOrderEvent()
                        .orderId(orderId)
                        .customerId(customerId)
                        .returnedFunds(returnedFunds)
                        .returnedStock(returnedStock)
        );
    }

    public @NotNull DeliverOrderEvent asDeliverOrderEvent(
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
    public @NotNull DiscardedEvent discard(@NotNull @Valid DomainEvent event) {
        return DiscardedEvent.discard(event);
    }

    public @NotNull DomainEvent discard() {
        return DiscardedEvent.discard(event);
    }

    public @NotNull DomainEvent discard(@NotNull String context) {
        return DiscardedEvent.discard(event, context);
    }
}
