package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.events.DomainEventTracer;
import com.jbazann.commons.async.transactions.TransactionPhaseExecutor;
import com.jbazann.commons.async.transactions.TransactionResult;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

import static com.jbazann.commons.async.events.DomainEvent.Type.*;

public final class DomainEventProcessorService {

    private final ApplicationMember member;
    private final TransactionPhaseExecutor transactionPhaseExecutor;
    private final TransactionResponseProvider transactionResponseService;
    private final DomainEventTracer tracer;
    private final List<DomainEvent.Type> ACTION_EVENTS = List.of(REQUEST, COMMIT, ROLLBACK);

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

        if (ACTION_EVENTS.contains(event.type())) {
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
