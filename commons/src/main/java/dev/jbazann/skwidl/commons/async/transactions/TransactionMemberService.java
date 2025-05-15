package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
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
    private final DomainEventBuilder builder;
    private final List<DomainEvent.Type> ACTION_EVENTS = List.of(REQUEST, COMMIT, ROLLBACK);

    public TransactionMemberService(TransactionStageExecutorService executor, ApplicationMember member, TransactionResponseService response, RabbitPublisher publisher, DomainEventBuilder builder) {
        this.member = member;
        this.executor = executor;
        this.response = response;
        this.publisher = publisher;
        this.builder = builder;
    }

    public void handleEvent(@NotNull @Valid DomainEvent event) {
        if (handleNotAMember(event)) return;
        if (handleNotRelevantEventType(event)) return;

        if (ACTION_EVENTS.contains(event.type())) {
            final TransactionResult result = executor.runStageFor(event);
            response.sendResponse(event, result);
        } else {
            publisher.publish(builder.discard(event));
        }
    }

    private boolean handleNotAMember(DomainEvent event) {
        if (!event.transaction().quorum().isMember(member)) {
            publisher.publish(builder.forEvent(event)
                    .discard("Not a quorum member."));
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
            publisher.publish(builder.discard(event));
            return true;
        }
        return false;
    }

}
