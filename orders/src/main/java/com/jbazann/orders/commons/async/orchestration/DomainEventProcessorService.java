package com.jbazann.orders.commons.async.orchestration;

import com.jbazann.orders.commons.async.events.DiscardedEvent;
import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.async.events.DomainEventTracer;
import com.jbazann.orders.commons.async.transactions.TransactionPhaseExecutor;
import com.jbazann.orders.commons.async.transactions.TransactionResult;
import com.jbazann.orders.commons.identity.ApplicationMember;
import com.jbazann.orders.commons.async.rabbitmq.RabbitPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

import static com.jbazann.orders.commons.async.events.DomainEvent.Type.*;

public final class DomainEventProcessorService {

    private final RabbitPublisher publisher;
    private final ApplicationMember member;
    private final TransactionPhaseExecutor transactionPhaseExecutor;
    private final TransactionResponseProvider transactionResponseProvider;
    private final DomainEventTracer tracer;

    public DomainEventProcessorService(RabbitPublisher publisher, TransactionPhaseExecutor transactionPhaseExecutor, ApplicationMember member, TransactionResponseProvider transactionResponseProvider, DomainEventTracer tracer) {
        this.publisher = publisher;
        this.member = member;
        this.transactionPhaseExecutor = transactionPhaseExecutor;
        this.transactionResponseProvider = transactionResponseProvider;
        this.tracer = tracer;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.event}")
    public void eventMessage(DomainEvent event) {
        if (handleNotAMember(event)) return;
        if (handleNotRelevantEventType(event)) return;


        final List<DomainEvent.Type> actionEvents = List.of(REQUEST, COMMIT, ROLLBACK);
        if (actionEvents.contains(event.type())) {
            final TransactionResult result = transactionPhaseExecutor.run(event);
            final DomainEvent response = transactionResponseProvider.getResponse(event, result);
            publisher.publish(response);
            return;
        }
        discardEvent(event);
    }

    public void discardEvent(DomainEvent event, String context) {
        publisher.publish(tracer.discard(event, context));
    }

    public void discardEvent(DomainEvent event) {
        publisher.publish(tracer.discard(event, "No action required."));
    }

    private boolean handleNotAMember(DomainEvent event) {
        if (!event.transactionQuorum().isMember(member)) {
            publisher.publish(tracer.discard(event, "Not a quorum member."));
            return true;
        }
        return false;
    }

    private boolean handleNotRelevantEventType(DomainEvent event) {
        final boolean shouldDiscard = switch (event.type()) {
            case ACK, ERROR, DISCARD, UNKNOWN -> true;
            case ACCEPT, REQUEST, COMMIT, REJECT, ROLLBACK -> false;
        };
        if (shouldDiscard) {
            publisher.publish(tracer.discard(event, "No action required for event type."));
            return true;
        }
        return false;
    }
}
