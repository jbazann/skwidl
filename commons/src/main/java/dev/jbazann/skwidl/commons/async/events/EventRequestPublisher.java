package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;

public class EventRequestPublisher {

    private final RabbitPublisher publisher;
    private final DomainEventBuilder builder;

    public EventRequestPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        this.publisher = publisher;
        this.builder = builder;
    }

    public void request(DomainEventBuilder builder, String context) {
        publisher.publish(builder.withType(DomainEvent.Type.REQUEST,context).asDomainEvent());
    }

    public void request(DomainEvent event, String context) {
        publisher.publish(builder.forEvent(event).withType(DomainEvent.Type.REQUEST, context).asDomainEvent());
    }

}
