package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class TransactionResponseService {

    private final RabbitPublisher publisher;
    private final DomainEventBuilderFactory events;


    public TransactionResponseService(RabbitPublisher publisher, DomainEventBuilderFactory events) {
        this.publisher = publisher;
        this.events = events;
    }

    public void sendResponse(@NotNull @Valid DomainEvent event,
                             @NotNull @Valid TransactionResult result) {
        DomainEventBuilder<DomainEvent> response = events.wrap(event).setContext(result.getContext());
        switch (event.getType()) {
            case ROLLBACK -> responseForRollback(response, result);
            case COMMIT -> responseForCommit(response, result);
            case REQUEST -> responseForRequest(response, result);
            default -> throw new IllegalArgumentException(
                    "Attempted to evaluate TransactionResult for a non-transactional event.");
        }
    }

    private void responseForRequest(DomainEventBuilder<DomainEvent> response, TransactionResult result) {
        switch (result.getSimpleResult()) {
            case SUCCESS -> publisher.publish(response.setType(DomainEvent.Type.ACCEPT).build());
            case FAILURE, REGISTRY_FAILURE -> publisher.publish(response.setType(DomainEvent.Type.REJECT).build());
            case CRITICAL_FAILURE -> {
                publisher.publish(response.setType(DomainEvent.Type.ERROR).build());
                publisher.publish(response.setType(DomainEvent.Type.REJECT).build());// TODO this should publish only once, to both routes
            }
        }
    }

    private void responseForCommit(DomainEventBuilder<DomainEvent> response, TransactionResult result) {
        /*
         * Ack regardless of result because at this point rolling back is not
         * possible.
         * // TODO evaluate recovery measures for errors during commit phase.
         */
        publisher.publish(response.setType(DomainEvent.Type.ACK).build());
    }

    private void responseForRollback(DomainEventBuilder<DomainEvent> response, TransactionResult result) {
        switch (result.getSimpleResult()) {
            case SUCCESS -> publisher.publish(response.setType(DomainEvent.Type.ACK).build());
            case FAILURE,  CRITICAL_FAILURE, REGISTRY_FAILURE  ->
                    publisher.publish(response.setType(DomainEvent.Type.ERROR).build());
        }
    }


}
