package com.jbazann.orders.commons.rabbitmq;

import com.jbazann.orders.commons.events.DomainEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;


@Service
public class RabbitPublisher {

    @Value("${jbazann.rabbit.exchanges.orders}")
    public final String ORDERS = "";
    @Value("${jbazann.rabbit.queues.saga}")
    public final String SAGA = "";

    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    public RabbitPublisher(final RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    public void publish(final @NotNull DomainEvent event, final @NotNull String routingKey, final @NotNull String exchange) {
        rabbitMessagingTemplate.send(
                exchange,
                routingKey,
                new GenericMessage<>(event)
        );
    }

}
