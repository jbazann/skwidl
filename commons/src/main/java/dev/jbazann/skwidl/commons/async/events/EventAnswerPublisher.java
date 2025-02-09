package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import static dev.jbazann.skwidl.commons.async.events.DomainEvent.Type.*;

/**
 * A bunch of common responses that save me three to five keystrokes whenever
 * I need them.
 */
@Validated
public class EventAnswerPublisher {

    private final RabbitPublisher publisher;
    private final @NotNull DomainEventBuilder builder;

    public EventAnswerPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        this.publisher = publisher;
        this.builder = builder;
    }

    public void request(@NotNull @Valid DomainEventBuilder builder,@NotNull String context) {
        publish(builder, REQUEST, context);
    }

    public void request(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, REQUEST, context);
    }

    public void acknowledge(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, ACK, context);
    }

    public void acknowledge(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, ACK, context);
    }

    public void reject(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, REJECT, context);
    }

    public void reject(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, REJECT, context);
    }

    public void rollback(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, ROLLBACK, context);
    }

    public void rollback(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, ROLLBACK, context);
    }

    public void accept(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, ACCEPT, context);
    }

    public void accept(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, ACCEPT, context);
    }

    public void commit(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, COMMIT, context);
    }

    public void commit(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, COMMIT, context);
    }

    public void error(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, ERROR, context);
    }

    public void error(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, ERROR, context);
    }

    public void warn(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publish(builder, WARNING, context);
    }

    public void warn(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publish(event, WARNING, context);
    }

    public void discard(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publisher.publish(builder.discard(context));
    }

    public void discard(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publisher.publish(builder.forEvent(event).discard(context));
    }

    private void publish(DomainEventBuilder builder,
                         DomainEvent.Type type,
                         String context) {
        publisher.publish(builder.withType(type,context).asDomainEvent());
    }

    private void publish(DomainEvent event,
                         DomainEvent.Type type,
                         String context) {
        publisher.publish(builder.forEvent(event).withType(type, context).asDomainEvent());
    }




}
