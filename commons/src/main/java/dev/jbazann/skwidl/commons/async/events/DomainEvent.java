package dev.jbazann.skwidl.commons.async.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
public abstract class DomainEvent {

    @NotNull
    private UUID id;
    @NotNull
    private LocalDateTime timestamp;
    @NotNull
    private ApplicationMember sentBy;
    @NotNull @Valid
    private Transaction transaction;
    @NotNull
    private Type type;
    @NotNull
    private String context;

    /**
     * Initializes a shallow copy of an event, preserving subclass data.
     * @param event the event to copy.
     * @return a new event with a different identity, but the same data as the argument.
     */
    public static @NotNull @Valid DomainEvent copyOf(DomainEvent event) {
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
     */
    protected abstract DomainEvent copy();

    /**
     * Sets the carried-over and subclass fields as sensible
     * to respond to event.
     * @param event the event to respond to.
     * @return the event this method is called on, with its fields 
     * initialized to respond to event.
     */
    public DomainEvent answer(DomainEvent event) {
        transaction(event.transaction());
        context("Reply to " + event.id());
        return _answer(event);
    }

    /**
     * Internal method for use within {@link DomainEvent#answer(DomainEvent)}.
     * Implementations should initialize all their fields to valid values 
     * that are sensible for a reply to event. <br>
     * Overriding implementations don't need to set the values of inherited attributes.
     * @param event the event to respond to.
     * @return the event this method is called on, with its fields 
     * initialized to respond to event.
     */
    protected abstract DomainEvent _answer(DomainEvent event);

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
         * For logging purposes. To be emitted by transaction coordinators
         * to summarize operations.
         */
        TRACE("log.trace.event"),
        /**
         * For logging purposes only, signals an unrecoverable state that
         * should never have been possible.
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
