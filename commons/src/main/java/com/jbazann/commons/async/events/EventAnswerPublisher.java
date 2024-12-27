package com.jbazann.commons.async.events;

import com.jbazann.commons.async.rabbitmq.RabbitPublisher;

import static com.jbazann.commons.async.events.DomainEvent.Type.*;

/**
 * A bunch of common responses that save me 3 to 5 keystrokes whenever
 * I need them.
 */
public class EventAnswerPublisher {

    private final RabbitPublisher publisher;
    private final DomainEventBuilder builder;

    public EventAnswerPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        this.publisher = publisher;
        this.builder = builder;
    }

    public void request(DomainEventBuilder builder, String context) {
        publish(builder, REQUEST, context);
    }

    public void request(DomainEvent event, String context) {
        publish(event, REQUEST, context);
    }

    public void acknowledge(DomainEventBuilder builder, String context) {
        publish(builder, ACK, context);
    }

    public void acknowledge(DomainEvent event, String context) {
        publish(event, ACK, context);
    }

    public void reject(DomainEventBuilder builder, String context) {
        publish(builder, REJECT, context);
    }

    public void reject(DomainEvent event, String context) {
        publish(event, REJECT, context);
    }

    public void rollback(DomainEventBuilder builder, String context) {
        publish(builder, ROLLBACK, context);
    }

    public void rollback(DomainEvent event, String context) {
        publish(event, ROLLBACK, context);
    }

    public void accept(DomainEventBuilder builder, String context) {
        publish(builder, ACCEPT, context);
    }

    public void accept(DomainEvent event, String context) {
        publish(event, ACCEPT, context);
    }

    public void commit(DomainEventBuilder builder, String context) {
        publish(builder, COMMIT, context);
    }

    public void commit(DomainEvent event, String context) {
        publish(event, COMMIT, context);
    }

    public void error(DomainEventBuilder builder, String context) {
        publish(builder, ERROR, context);
    }

    public void error(DomainEvent event, String context) {
        publish(event, ERROR, context);
    }

    public void warn(DomainEventBuilder builder, String context) {
        publish(builder, WARNING, context);
    }

    public void warn(DomainEvent event, String context) {
        publish(event, WARNING, context);
    }

    public void discard(DomainEventBuilder builder, String context) {
        publisher.publish(builder.discard(context));
    }

    public void discard(DomainEvent event, String context) {
        publisher.publish(builder.forEvent(event).discard(context));
    }

    private void publish(DomainEventBuilder builder, DomainEvent.Type type, String context) {
        publisher.publish(builder.withType(type,context).asDomainEvent());
    }

    private void publish(DomainEvent event, DomainEvent.Type type, String context) {
        publisher.publish(builder.forEvent(event).withType(type, context).asDomainEvent());
    }




}
