package dev.jbazann.skwidl.customers.commons.rabbitmq;

import dev.jbazann.skwidl.customers.commons.events.DomainEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;


@Service
public class RabbitPublisher {

    @Value("${jbazann.rabbit.exchanges.customers}")
    public final String CUSTOMERS = "";
    @Value("${jbazann.rabbit.exchanges.orders}")
    public final String ORDERS = "";
    @Value("${jbazann.rabbit.exchanges.users}")
    public final String USERS = "";
    @Value("${jbazann.rabbit.queues.saga}")
    public final String SAGA = "";
    @Value("${jbazann.rabbit.queues.operation}")
    public final String OPERATION = "";

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
