package com.jbazann.commons.async.events;

import com.jbazann.commons.async.transactions.TransactionQuorum;
import com.jbazann.commons.identity.ApplicationMember;
import com.jbazann.commons.utils.TimeProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public abstract class DomainEvent {

    private UUID id;
    private LocalDateTime timestamp;
    private ApplicationMember sentBy;
    private Transaction transaction;
    private Type type;
    private String context;

    public static DomainEvent init(final DomainEvent event) {
        final TransactionQuorum emptyQuorum = new TransactionQuorum()
                .members(List.of())
                .coordinator(new ApplicationMember(""));
        final Transaction transaction = new Transaction()
                .id(UUID.randomUUID()) // TODO replace with safe alternative
                .quorum(emptyQuorum)
                .expires(TimeProvider.localDateTimeNow().plusHours(1L)); // TODO configure expiring time
        return event.id(UUID.randomUUID()) // TODO replace with safe alternative
                .transaction(transaction)
                .timestamp(TimeProvider.localDateTimeNow())
                .type(Type.UNKNOWN)
                .context("Initialized by .commons.async.events.DomainEvent.init");
    }

    /**
     * Constructs a copy of an event, preserving subclass data.
     * @param event the event to copy.
     * @return a new event with a different identity, but same data as the argument.
     */
    public static DomainEvent copyOf(DomainEvent event) {
        return event.copy()
                .id(UUID.randomUUID()) // TODO replace with safe alternative
                .transaction(event.transaction())
                .timestamp(TimeProvider.localDateTimeNow()) // TODO replace with safe alternative
                .sentBy(event.sentBy())
                .type(event.type())
                .context("Copied by .commons.async.events.DomainEvent.copyOf");
    }

    /**
     * Internal method for use within {@link DomainEvent#copyOf(DomainEvent)}.
     * Implementations should instantiate a new event of the implementing type,
     * with new values for any attributes intended to identify an event from others,
     * but with copies of the rest. <br>
     * Overriding implementations don't need to set the values of inherited attributes.
     * @return a copy of the event this method was called on.
     * // TODO evaluate the need for deep copies.
     */
    protected abstract DomainEvent copy();

    @Data
    public static final class Transaction {
        private UUID id;
        private TransactionQuorum quorum;
        private LocalDateTime expires;
    }

    @Getter
    @Accessors(fluent = true)
    public enum Type{
        REQUEST("request.event"),
        ACCEPT("accept.event"),
        REJECT("reject.event"),
        ROLLBACK("rollback.event"),
        COMMIT("commit.event"),
        DISCARD("log.discard.event"),
        WARNING("warning.event"),
        /**
         * For logging purposes. To be emitted after a successful commit.
         */
        ACK("ack.event"),
        /**
         * For when a rollback should ensue. I will not implement rollbacks.
         * TODO: maybe look into this.
         */
        ERROR("error.error.error"),
        /**
         * Null-safe placeholder.
         */
        UNKNOWN("error.null.event");

        public final String routingKey;

        Type(String routingKey) {
            this.routingKey = routingKey;
        }

    }

}
