package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionCoordinatorService;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransactionRepository;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
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
        // TODO study routing alternatives to eliminate the need for this check
        if(!coordinator.isCoordinatorFor(event)) return;

        coordinator.handle(event);
    }

}
