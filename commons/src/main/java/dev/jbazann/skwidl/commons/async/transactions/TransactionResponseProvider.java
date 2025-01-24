package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.EventAnswerPublisher;

public class TransactionResponseProvider {

    private final EventAnswerPublisher publisher;

    public TransactionResponseProvider(EventAnswerPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendResponse(DomainEvent event, TransactionResult result) {
        switch (event.type()) {
            case ROLLBACK -> responseForRollback(event, result);
            case COMMIT -> responseForCommit(event, result);
            case REQUEST -> responseForRequest(event, result);
            default -> throw new IllegalArgumentException(
                    "Attempted to evaluate TransactionResult for a non-transactional event.");
        };
    }

    private void responseForRequest(DomainEvent event, TransactionResult result) {
        switch (result.simpleResult()) {
            case SUCCESS -> publisher.accept(event, result.context());
            case FAILURE, REGISTRY_FAILURE -> publisher.reject(event, result.context());
            case CRITICAL_FAILURE -> {
                publisher.error(event, result.context());
                publisher.reject(event, result.context());// TODO this should publish only once, to both routes
            }
        };
    }

    private void responseForCommit(DomainEvent event, TransactionResult result) {
        /*
         * Ack regardless of result because at this point rolling back is not
         * possible.
         * // TODO evaluate recovery measures for errors during commit phase.
         */
        publisher.acknowledge(event, result.context());
    }

    private void responseForRollback(DomainEvent event, TransactionResult result) {
        switch (result.simpleResult()) {
            case SUCCESS -> publisher.acknowledge(event, result.context());
            case FAILURE,  CRITICAL_FAILURE, REGISTRY_FAILURE  -> publisher.error(event, result.context());
        };
    }


}
