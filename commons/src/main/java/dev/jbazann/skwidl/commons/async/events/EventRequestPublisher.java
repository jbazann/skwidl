package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class EventRequestPublisher {

    private final @NotNull RabbitPublisher publisher;
    private final @NotNull DomainEventBuilder builder;

    public EventRequestPublisher(RabbitPublisher publisher, DomainEventBuilder builder) {
        this.publisher = publisher;
        this.builder = builder;
    }

    public void request(@NotNull @Valid DomainEventBuilder builder, @NotNull String context) {
        publisher.publish(builder.withType(DomainEvent.Type.REQUEST,context).asDomainEvent());
    }

    public void request(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publisher.publish(builder.forEvent(event).withType(DomainEvent.Type.REQUEST, context).asDomainEvent());
    }

}
