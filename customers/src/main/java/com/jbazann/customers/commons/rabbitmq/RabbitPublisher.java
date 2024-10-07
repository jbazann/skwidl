package com.jbazann.customers.commons.rabbitmq;

import com.jbazann.customers.commons.events.DomainEvent;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;


@Service
public class RabbitPublisher {

    @Value("${jbazann.rabbit.exchanges.customers}")
    private final String CUSTOMERS_EXCHANGE = "";
    @Value("${jbazann.rabbit.exchanges.orders}")
    private final String ORDERS_EXCHANGE = "";
    @Value("${jbazann.rabbit.exchanges.users}")
    private final String USERS_EXCHANGE = "";
    @Value("${jbazann.rabbit.queues.saga}")
    private final String SAGA_QUEUE = "";
    @Value("${jbazann.rabbit.queues.operation}")
    private final String OPERATION_QUEUE = "";

    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    public RabbitPublisher(final RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    private void publish(final @NotNull DomainEvent event,final @NotNull String routingKey, final @NotNull String exchange) {
        rabbitMessagingTemplate.send(
                exchange,
                routingKey,
                new GenericMessage<>(event)
        );
    }

    public void customersSaga(final @NotNull DomainEvent event) {
        publish(event, SAGA_QUEUE, CUSTOMERS_EXCHANGE);
    }

    public void ordersSaga(final @NotNull DomainEvent event) {
        publish(event, SAGA_QUEUE, ORDERS_EXCHANGE);
    }

    public void usersSaga(final @NotNull DomainEvent event) {
        publish(event, SAGA_QUEUE, USERS_EXCHANGE);
    }

    public void customersOperation(final @NotNull DomainEvent event) {
        publish(event, OPERATION_QUEUE, CUSTOMERS_EXCHANGE);
    }

    public void ordersOperation(final @NotNull DomainEvent event) {
        publish(event, OPERATION_QUEUE, ORDERS_EXCHANGE);
    }

    public void usersOperation(final @NotNull DomainEvent event) {
        publish(event, OPERATION_QUEUE, USERS_EXCHANGE);
    }

}
