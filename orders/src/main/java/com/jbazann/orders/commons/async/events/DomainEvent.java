package com.jbazann.orders.commons.async.events;

import com.jbazann.orders.commons.async.transactions.TransactionQuorum;
import com.jbazann.orders.commons.identity.ApplicationMember;
import com.jbazann.orders.commons.utils.TimeProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class DomainEvent {

    private UUID id;
    private UUID transactionId;
    private LocalDateTime timestamp;
    private TransactionQuorum transactionQuorum;
    private ApplicationMember sentBy;
    private Type type;
    private String routingKey;
    private String context;

    public DomainEvent() {
        this.id = UUID.randomUUID(); // TODO replace with safe alternative
        this.transactionId = UUID.randomUUID(); // TODO replace with safe alternative
        this.timestamp = TimeProvider.localDateTimeNow();
        this.transactionQuorum = new TransactionQuorum();
        this.type = Type.UNKNOWN;
        this.routingKey = Type.UNKNOWN.routingKey;
        this.context = "";
    }

    /**
     * Update ID fields to publish the event as a new one without losing subtype data.
     */
    public static DomainEvent reEmit(DomainEvent event) { // TODO this is ugly.
        return event.id(UUID.randomUUID())
                .timestamp(TimeProvider.localDateTimeNow());
    }

    public DomainEvent setRouting() {
        routingKey = type.routingKey;
        return this;
    }

    public enum Type{
        REQUEST("request.event"),
        ACCEPT("accept.event"),
        REJECT("reject.event"),
        ROLLBACK("rollback.event"),
        COMMIT("commit.event"),
        DISCARD("log.discard.event"),
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

        private final String routingKey;

        Type(String routingKey) {
            this.routingKey = routingKey;
        }

    }

}
