package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.validation.annotation.Validated;

@Validated
public class RabbitPublisher {

    @Value("${jbazann.rabbit.exchanges.event}")
    public final String EVENT_XCHNG = "";
    @Value("${jbazann.rabbit.queues.event}")
    public final String EVENT_Q = "";

    private final DomainEventBuilderFactory events;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    public RabbitPublisher(DomainEventBuilderFactory events, RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.events = events;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    public void publish(@NotNull @Valid DomainEvent event) {
        rabbitMessagingTemplate.send(
                EVENT_XCHNG,
                event.getType().getRoutingKey(),
                new GenericMessage<>(event)
        );
    }

    public void publish(DomainEvent event,
                        DomainEvent.Type type,
                        String context) {
        rabbitMessagingTemplate.send(
                EVENT_XCHNG,
                type.getRoutingKey(),
                new GenericMessage<>(events
                        .wrap(event)
                        .setType(type, context)
                        .build())
        );
    }


}
