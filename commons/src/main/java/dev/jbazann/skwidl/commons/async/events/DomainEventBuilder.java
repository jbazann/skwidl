package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.events.specialized.CancelAcceptedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DiscardedEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionQuorum;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.commons.exceptions.CommonsInternalException;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
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

@ToString
@Validated
public class DomainEventBuilder<Type extends DomainEvent> {

    private final Type event;
    private final @NotNull @Valid ApplicationMember thisApplication;
    private final @NotNull @Valid ApplicationMember defaultCoordinator;
    private final Logger log = LoggerFactory.get(getClass());

    public DomainEventBuilder(ApplicationMember identity, ApplicationMember defaultCoordinator, Class<Type> eventClass) {
        log.method(identity,defaultCoordinator,eventClass);
        this.thisApplication = identity;
        this.defaultCoordinator = defaultCoordinator;
        try {
            event = eventClass.cast(DomainEvent.init(eventClass.getConstructor().newInstance()));
        } catch (ClassCastException |
                 NoSuchMethodException |
                 IllegalAccessException |
                 InstantiationException |
                 InvocationTargetException e
        ) {
            throw new CommonsInternalException("Exception while trying to instantiate " + eventClass,e);
        }
        event.setSentBy(thisApplication);
        event.getTransaction().getQuorum().setCoordinator(defaultCoordinator);
        event.getTransaction().getQuorum().setMembers(List.of(thisApplication));
    }

    public DomainEventBuilder(ApplicationMember identity, ApplicationMember defaultCoordinator, Type event) {
        log.method(identity,defaultCoordinator,event);
        this.thisApplication = identity;
        this.defaultCoordinator = defaultCoordinator;
        this.event = event;
    }

    public @NotNull @Valid DomainEventBuilder<Type> answer(
            DomainEvent event
    ) {
        log.method(event);
        this.event.setContext("Answer to " + event.getId())
                .setTransaction(event.getTransaction());
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setType(@NotNull DomainEvent.Type type) {
        log.method(type);
        event.setType(type);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setType(@NotNull DomainEvent.Type type, @NotNull String context) {
        log.method(type,context);
        event.setType(type).setContext(context);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setContext(@NotNull String context) {
        log.method(context);
        event.setContext(context);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setContext(@NotNull String format, @NotNull Object... args) {
        log.method(format,args);
        event.setContext(String.format(format,args));
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setQuorumMembers(@NotNull @NotEmpty List<@NotNull ApplicationMember> quorum) {
        log.method(quorum);
        event.getTransaction().getQuorum().setMembers(quorum);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setCoordinator(@NotNull ApplicationMember coordinator) {
        log.method(coordinator);
        event.getTransaction().getQuorum().setCoordinator(coordinator);
        log.result(this);
        return this;
    }

    public @NotNull DomainEventBuilder<Type> setTransaction(@NotNull @Valid Transaction transaction) {
        log.method(transaction);
        event.setTransaction(transaction);
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
        event.setTransaction(new Transaction()
                .setId(id)
                .setQuorum(quorum)
                .setStatus(status)
                .setExpires(expires)
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
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setReturnedFunds(returnedFunds);
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
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setReturnedFunds(returnedFunds)
                .setReturnedStock(returnedStock);
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
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setReturnedFunds(returnedFunds);
        log.result(this);
        return this;
    }

    /**
     * Wrapper for consistency. See {@link DiscardedEvent#discard(DomainEvent)}
     */
    public @NotNull @Valid DiscardedEvent discard(@NotNull @Valid DomainEvent event) {
        return log.result(DiscardedEvent.discard(event));
    }

    public @NotNull @Valid DomainEvent discard() {
        return log.result(DiscardedEvent.discard(event));
    }

    public @NotNull @Valid DomainEvent discard(@NotNull String context) {
        return log.result(DiscardedEvent.discard(event, context));
    }
}
