package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class TransactionResponseService {

    private final RabbitPublisher publisher;
    private final DomainEventBuilder builder;

    public TransactionResponseService(RabbitPublisher publisher, DomainEventBuilder builder) {
        this.publisher = publisher;
        this.builder = builder;
    }

    public void sendResponse(@NotNull @Valid DomainEvent event,
                             @NotNull @Valid TransactionResult result) {
        DomainEventBuilder response = builder.forEvent(event).withContext(result.context());
        switch (event.type()) {
            case ROLLBACK -> responseForRollback(response, result);
            case COMMIT -> responseForCommit(response, result);
            case REQUEST -> responseForRequest(response, result);
            default -> throw new IllegalArgumentException(
                    "Attempted to evaluate TransactionResult for a non-transactional event.");
        }
    }

    private void responseForRequest(DomainEventBuilder response, TransactionResult result) {
        switch (result.simpleResult()) {
            case SUCCESS -> publisher.publish(response.withType(DomainEvent.Type.ACCEPT).asDomainEvent());
            case FAILURE, REGISTRY_FAILURE -> publisher.publish(response.withType(DomainEvent.Type.REJECT).asDomainEvent());
            case CRITICAL_FAILURE -> {
                publisher.publish(response.withType(DomainEvent.Type.ERROR).asDomainEvent());
                publisher.publish(response.withType(DomainEvent.Type.REJECT).asDomainEvent());// TODO this should publish only once, to both routes
            }
        }
    }

    private void responseForCommit(DomainEventBuilder response, TransactionResult result) {
        /*
         * Ack regardless of result because at this point rolling back is not
         * possible.
         * // TODO evaluate recovery measures for errors during commit phase.
         */
        publisher.publish(response.withType(DomainEvent.Type.ACK).asDomainEvent());
    }

    private void responseForRollback(DomainEventBuilder response, TransactionResult result) {
        switch (result.simpleResult()) {
            case SUCCESS -> publisher.publish(response.withType(DomainEvent.Type.ACK).asDomainEvent());
            case FAILURE,  CRITICAL_FAILURE, REGISTRY_FAILURE  ->
                    publisher.publish(response.withType(DomainEvent.Type.ERROR).asDomainEvent());
        }
    }


}
