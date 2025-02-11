package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionCoordinatorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.validation.annotation.Validated;

@Validated
public class RabbitCoordinatorListenerService {

    private final TransactionCoordinatorService coordinator;

    public RabbitCoordinatorListenerService(TransactionCoordinatorService coordinator) {
        this.coordinator = coordinator;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.coordination.event}")
    public void eventMessage(@NotNull @Valid DomainEvent event) {
        // TODO concurrent consumers
        coordinator.handleEvent(event);
    }

}
