package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.EventAnswerPublisher;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;

import java.util.List;

import static dev.jbazann.skwidl.commons.async.events.DomainEvent.Type.*;

public class TransactionMemberService {

    private final ApplicationMember member;
    private final TransactionStageExecutorService transactionStageExecutorService;
    private final TransactionResponseProvider transactionResponseService;
    private final EventAnswerPublisher publisher;
    private final List<DomainEvent.Type> ACTION_EVENTS = List.of(REQUEST, COMMIT, ROLLBACK);

    public TransactionMemberService(TransactionStageExecutorService transactionStageExecutorService, ApplicationMember member, TransactionResponseProvider transactionResponseService, EventAnswerPublisher publisher) {
        this.member = member;
        this.transactionStageExecutorService = transactionStageExecutorService;
        this.transactionResponseService = transactionResponseService;
        this.publisher = publisher;
    }

    public void handleEvent(DomainEvent event) {
        if (handleNotAMember(event)) return;
        if (handleNotRelevantEventType(event)) return;

        if (ACTION_EVENTS.contains(event.type())) {
            final TransactionResult result = transactionStageExecutorService.runPhaseFor(event);
            transactionResponseService.sendResponse(event, result);
        } else {
            publisher.discard(event, "No action required.");
        }
    }

    private boolean handleNotAMember(DomainEvent event) {
        if (!event.transaction().quorum().isMember(member)) {
            publisher.discard(event, "Not a quorum member.");
            return true;
        }
        return false;
    }

    private boolean handleNotRelevantEventType(DomainEvent event) {
        final boolean shouldDiscard = switch (event.type()) {
            case ACK, WARNING, ERROR, DISCARD, UNKNOWN -> true;
            case ACCEPT, REQUEST, COMMIT, REJECT, ROLLBACK -> false;
        };
        if (shouldDiscard) {
            publisher.discard(event, "No action required for event type.");
            return true;
        }
        return false;
    }

}
