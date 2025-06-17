package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class EventRequestPublisher {

    private final @NotNull RabbitPublisher publisher;
    private final @NotNull DomainEventBuilderFactory events;

    public EventRequestPublisher(RabbitPublisher publisher, DomainEventBuilderFactory events) {
        this.publisher = publisher;
        this.events = events;
    }

    public <T extends DomainEvent> void request(@NotNull DomainEventBuilder<T> builder, @NotNull String context) {
        publisher.publish(builder.setType(DomainEvent.Type.REQUEST,context).build());
    }

    public void request(@NotNull @Valid DomainEvent event, @NotNull String context) {
        publisher.publish(events.wrap(event).setType(DomainEvent.Type.REQUEST, context).build());
    }

}
