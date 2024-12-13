package com.jbazann.orders.commons.async.orchestration;

import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.async.events.DomainEventTracer;
import com.jbazann.orders.commons.async.transactions.TransactionPhaseExecutor;
import com.jbazann.orders.commons.async.transactions.TransactionResult;
import com.jbazann.orders.commons.identity.ApplicationMember;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

import static com.jbazann.orders.commons.async.events.DomainEvent.Type.*;

public final class DomainEventProcessorService {

    private final ApplicationMember member;
    private final TransactionPhaseExecutor transactionPhaseExecutor;
    private final TransactionResponseProvider transactionResponseService;
    private final DomainEventTracer tracer;

    public DomainEventProcessorService(TransactionPhaseExecutor transactionPhaseExecutor, ApplicationMember member, TransactionResponseProvider transactionResponseService, DomainEventTracer tracer) {
        this.member = member;
        this.transactionPhaseExecutor = transactionPhaseExecutor;
        this.transactionResponseService = transactionResponseService;
        this.tracer = tracer;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.event}")
    public void eventMessage(DomainEvent event) {
        if (handleNotAMember(event)) return;
        if (handleNotRelevantEventType(event)) return;


        final List<DomainEvent.Type> actionEvents = List.of(REQUEST, COMMIT, ROLLBACK);
        if (actionEvents.contains(event.type())) {
            final TransactionResult result = transactionPhaseExecutor.run(event);
            transactionResponseService.sendResponse(event, result);
            return;
        }
        discardEvent(event);
    }

    public void discardEvent(DomainEvent event, String context) {
        tracer.discard(event, context);
    }

    public void discardEvent(DomainEvent event) {
        discardEvent(event, "No action required.");
    }

    private boolean handleNotAMember(DomainEvent event) {
        if (!event.transaction().quorum().isMember(member)) {
            tracer.discard(event, "Not a quorum member.");
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
            tracer.discard(event, "No action required for event type.");
            return true;
        }
        return false;
    }
}
