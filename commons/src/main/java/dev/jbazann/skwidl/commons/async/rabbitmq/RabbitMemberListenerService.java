package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.validation.annotation.Validated;

@Validated
public class RabbitMemberListenerService {

    private final TransactionMemberService member;

    public RabbitMemberListenerService(TransactionMemberService member) {
        this.member = member;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.event}")
    public void eventMessage(@NotNull @Valid DomainEvent event) {
        // TODO concurrent consumers
        member.handleEvent(event);
    }

}
