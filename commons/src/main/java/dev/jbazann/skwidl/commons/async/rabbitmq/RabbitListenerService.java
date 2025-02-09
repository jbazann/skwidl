package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionCoordinatorService;
import dev.jbazann.skwidl.commons.async.transactions.TransactionMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.validation.annotation.Validated;

@Validated
public final class RabbitListenerService {

    private final TransactionCoordinatorService coordinator;
    private final TransactionMemberService member;

    public RabbitListenerService(TransactionCoordinatorService coordinator, TransactionMemberService member) {
        this.coordinator = coordinator;
        this.member = member;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.event}")
    //TODO manual acknowledgement ?
    public void eventMessage(@NotNull @Valid DomainEvent event) {
        // TODO messages are not yet consumed when coordinator returns from handleEvent.
        if (coordinator != null) coordinator.handleEvent(event);
        if (member != null) member.handleEvent(event);
    }

}
