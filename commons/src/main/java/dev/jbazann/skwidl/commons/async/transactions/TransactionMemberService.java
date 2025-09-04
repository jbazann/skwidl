package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static dev.jbazann.skwidl.commons.async.events.DomainEvent.Type.*;

@Validated
public class TransactionMemberService {

    private final ApplicationMember member;
    private final TransactionStageExecutorService executor;
    private final TransactionResponseService response;
    private final RabbitPublisher publisher;
    private final DomainEventBuilderFactory events;
    private final List<DomainEvent.Type> ACTION_EVENTS = List.of(REQUEST, COMMIT, ROLLBACK);
    private final Logger log = LoggerFactory.get(TransactionMemberService.class);

    public TransactionMemberService(TransactionStageExecutorService executor, ApplicationMember member, TransactionResponseService response, RabbitPublisher publisher, DomainEventBuilderFactory events) {
        this.member = member;
        this.executor = executor;
        this.response = response;
        this.publisher = publisher;
        this.events = events;
    }

    public void handleEvent(@NotNull @Valid DomainEvent event) {
        log.method(event);
        if (handleNotAMember(event)) {
            log.debug("Not a quorum member.");
            return;
        }
        if (handleNotRelevantEventType(event)) {
            log.debug("Not a relevant event type.");
            return;
        }

        log.debug("Action events: {} — Event type: {}", ACTION_EVENTS, event.type());
        if (ACTION_EVENTS.contains(event.type())) {
            final TransactionResult result = executor.runStageFor(event);
            response.sendResponse(event, result);
        } else {
            publisher.publish(events.wrap(event).discard());
        }
    }

    private boolean handleNotAMember(DomainEvent event) {
        if (!event.transaction().quorum().isMember(member)) {
            publisher.publish(events.wrap(event)
                    .discard("Not a quorum member."));
            return true;
        }
        return false;
    }

    private boolean handleNotRelevantEventType(DomainEvent event) {
        final boolean shouldDiscard = switch (event.type()) {
            case ACK, TRACE, WARNING, ERROR, DISCARD, UNKNOWN -> true;
            case ACCEPT, REQUEST, COMMIT, REJECT, ROLLBACK -> false;
        };
        if (shouldDiscard) {
            publisher.publish(events.wrap(event).discard());
            return true;
        }
        return false;
    }

}
