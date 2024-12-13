package com.jbazann.orders.commons.async.rabbitmq;

import com.jbazann.orders.commons.async.events.DomainEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

public class RabbitPublisher {

    @Value("${jbazann.rabbit.exchanges.event}")
    public final String EVENT_XCHNG = "";
    @Value("${jbazann.rabbit.queues.orders.event}")
    public final String EVENT_Q = "";

    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    public RabbitPublisher(final RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    public void publish(final @NotNull DomainEvent event) {
        rabbitMessagingTemplate.send(
                EVENT_XCHNG,
                event.routingKey(),
                new GenericMessage<>(event)
        );
    }

}
