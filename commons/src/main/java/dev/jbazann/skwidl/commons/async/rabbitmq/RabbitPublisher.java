package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
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

    private final DomainEventBuilder builder;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    public RabbitPublisher(DomainEventBuilder builder, RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.builder = builder;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    public void publish(@NotNull @Valid DomainEvent event) {
        rabbitMessagingTemplate.send(
                EVENT_XCHNG,
                event.type().routingKey(),
                new GenericMessage<>(event)
        );
    }

    public void publish(DomainEvent event,
                        DomainEvent.Type type,
                        String context) {
        rabbitMessagingTemplate.send(
                EVENT_XCHNG,
                type.routingKey(),
                new GenericMessage<>(builder
                        .forEvent(event)
                        .withType(type, context)
                        .asDomainEvent())
        );
    }


}
