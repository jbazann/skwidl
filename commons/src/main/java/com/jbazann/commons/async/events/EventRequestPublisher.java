package com.jbazann.commons.async.events;

import com.jbazann.commons.async.rabbitmq.RabbitPublisher;

import static com.jbazann.commons.async.events.DomainEvent.Type.REQUEST;

public class EventRequestPublisher {

    private final RabbitPublisher publisher;
    private final DomainEventBuilder builder;

    public EventRequestPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        this.publisher = publisher;
        this.builder = builder;
    }

    public void request(DomainEventBuilder builder, String context) {
        publisher.publish(builder.withType(REQUEST,context).asDomainEvent());
    }

    public void request(DomainEvent event, String context) {
        publisher.publish(builder.forEvent(event).withType(REQUEST, context).asDomainEvent());
    }

}
