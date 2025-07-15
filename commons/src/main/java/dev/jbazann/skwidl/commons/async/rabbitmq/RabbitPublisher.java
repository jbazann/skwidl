package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.validation.annotation.Validated;

@Validated
@ToString
public class RabbitPublisher {

    private final String EVENT_EXCHANGE;

    private final DomainEventBuilderFactory events;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    public RabbitPublisher(
            DomainEventBuilderFactory events,
            RabbitMessagingTemplate rabbitMessagingTemplate,
            String eventExchange
    ) {
        this.events = events;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.EVENT_EXCHANGE = eventExchange;
    }

    public void publish(@NotNull @Valid DomainEvent event) {
        rabbitMessagingTemplate.send(
                EVENT_EXCHANGE,
                event.type().routingKey(),
                new GenericMessage<>(event)
        );
    }

    public void publish(DomainEvent event,
                        DomainEvent.Type type,
                        String context) {
        rabbitMessagingTemplate.send(
                EVENT_EXCHANGE,
                type.routingKey(),
                new GenericMessage<>(events
                        .wrap(event)
                        .setType(type, context)
                        .build())
        );
    }


}
