package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.events.specialized.CancelAcceptedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DiscardedEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import dev.jbazann.skwidl.commons.exceptions.CommonsInternalException;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@ToString
@Validated
public class DomainEventBuilder<Type extends DomainEvent> {

    private final Type event;
    private final @NotNull @Valid ApplicationMember thisApplication;
    private final @NotNull @Valid ApplicationMember defaultCoordinator;
    private final @NotNull Supplier<Transaction> transactions;
    private final Logger log = LoggerFactory.get(getClass());

    public DomainEventBuilder(
            ApplicationMember identity,
            ApplicationMember defaultCoordinator,
            Class<Type> eventClass,
            Supplier<Transaction> transactions
    ) {
        log.method(identity,defaultCoordinator,eventClass);
        this.thisApplication = identity;
        this.defaultCoordinator = defaultCoordinator;
        this.transactions = transactions;
        try {
            event = eventClass.cast(init(eventClass.getConstructor().newInstance()));
        } catch (ClassCastException |
                 NoSuchMethodException |
                 IllegalAccessException |
                 InstantiationException |
                 InvocationTargetException e
        ) {
            throw new CommonsInternalException("Exception while trying to instantiate " + eventClass,e);
        }
        event.sentBy(thisApplication);
        event.transaction().quorum().coordinator(defaultCoordinator);
        event.transaction().quorum().members(List.of(thisApplication));
        log.result(this);
    }

    public DomainEventBuilder(ApplicationMember identity, ApplicationMember defaultCoordinator, Type event, Supplier<Transaction> transactions) {
        log.method(identity,defaultCoordinator,event);
        this.thisApplication = identity;
        this.defaultCoordinator = defaultCoordinator;
        this.transactions = transactions;
        this.event = event;
        log.result(this);
    }

    public @NotNull @Valid DomainEventBuilder<Type> answer(
            DomainEvent event
    ) {
        log.method(event);
        this.event.answer(event);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setType(@NotNull DomainEvent.Type type) {
        log.method(type);
        event.type(type);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setType(@NotNull DomainEvent.Type type, @NotNull String context) {
        log.method(type,context);
        event.type(type).context(context);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setContext(@NotNull String context) {
        log.method(context);
        event.context(context);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setContext(@NotNull String format, @NotNull Object... args) {
        log.method(format,args);
        event.context(String.format(format,args));
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setQuorumMembers(@NotNull @NotEmpty List<@NotNull ApplicationMember> quorum) {
        log.method(quorum);
        event.transaction().quorum().members(quorum);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setCoordinator(@NotNull ApplicationMember coordinator) {
        log.method(coordinator);
        event.transaction().quorum().coordinator(coordinator);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setTransaction(@NotNull @Valid Transaction transaction) {
        log.method(transaction);
        event.transaction(transaction);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setTransaction(
            @NotNull UUID id,
            @NotNull @Valid TransactionQuorum quorum,
            @NotNull Transaction.TransactionStatus status,
            @NotNull LocalDateTime expires
    ) {
        log.method(id,quorum,status,expires);
        event.transaction(transactions.get()
                .id(id)
                .quorum(quorum)
                .status(status)
                .expires(expires)
        );
        log.result(this);
        return this;
    }

    public @NotNull @Valid Type build() {
        log.method();
        log.result(event);
        return event;
    }

    public <T extends DomainEvent> @NotNull @Valid T build(@NotNull Class<T> c) {
        return c.cast(build());
    }

    public @NotNull DomainEventBuilder<Type> asCancelAcceptedOrderEvent(
            @NotNull UUID orderId,
            @NotNull UUID customerId,
            @NotNull @Min(0) BigDecimal returnedFunds
    ) {
        log.method(orderId, customerId, returnedFunds);
        ((CancelAcceptedOrderEvent) event)
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> asCancelPreparedOrderEvent(
            @NotNull UUID orderId,
            @NotNull UUID customerId,
            @NotNull @Min(0) BigDecimal returnedFunds,
            @NotNull @NotEmpty Map<@NotNull UUID,@NotNull Integer> returnedStock
    ) {
        log.method(orderId, customerId, returnedFunds, returnedStock);
        ((CancelPreparedOrderEvent) event)
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds)
                .returnedStock(returnedStock);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> asDeliverOrderEvent(
            UUID orderId,
            UUID customerId,
            BigDecimal returnedFunds
    ) {
        log.method(orderId, customerId, returnedFunds);
        ((DeliverOrderEvent) event)
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds);
        log.result(this);
        return this;
    }

    public @NotNull @Valid DomainEvent discard() {
        return log.result(discard(event, "No action required."));
    }

    public @NotNull @Valid DomainEvent discard(@NotNull String context) {
        return log.result(discard(event, context));
    }

    private DiscardedEvent discard(DomainEvent event, String context) {
        DomainEvent e = init(new DiscardedEvent())
            .transaction(event.transaction())
            .sentBy(thisApplication)
            .context(context)
            .type(DomainEvent.Type.DISCARD);
        return log.result(((DiscardedEvent) e).discarded(event));
    }


    private DomainEvent init(final DomainEvent event) {
        final TransactionQuorum emptyQuorum = new TransactionQuorum()
                .members(List.of())
                .coordinator(new ApplicationMember(""));
        final Transaction transaction = transactions.get()
                .id(UUID.randomUUID()) // TODO replace with safe alternative
                .quorum(emptyQuorum)
                .status(Transaction.TransactionStatus.UNKNOWN)
                .expires(TimeProvider.localDateTimeNow().plusHours(1L)); // TODO configure expiring time
        return event.id(UUID.randomUUID()) // TODO replace with safe alternative
                .transaction(transaction)
                .timestamp(TimeProvider.localDateTimeNow())
                .type(DomainEvent.Type.UNKNOWN)
                .context("Initialized by .commons.async.events.DomainEvent.init");
    }

}
