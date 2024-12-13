package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.events.DomainEventTracer;
import com.jbazann.commons.async.transactions.TransactionResult;

public class TransactionResponseProvider {

    private final DomainEventTracer tracer;

    public TransactionResponseProvider(DomainEventTracer tracer) {
        this.tracer = tracer;
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
            case SUCCESS -> tracer.accept(event, result.context());
            case FAILURE, REGISTRY_FAILURE -> tracer.reject(event, result.context());
            case CRITICAL_FAILURE -> {
                tracer.error(event, result.context());
                tracer.reject(event, result.context());// TODO this should publish only once, to both routes
            }
        };
    }

    private void responseForCommit(DomainEvent event, TransactionResult result) {
        /*
         * Ack regardless of result because at this point rolling back is not
         * possible.
         * // TODO evaluate recovery measures for errors during commit phase.
         */
        tracer.acknowledge(event, result.context());
    }

    private void responseForRollback(DomainEvent event, TransactionResult result) {
        switch (result.simpleResult()) {
            case SUCCESS -> tracer.acknowledge(event, result.context());
            case FAILURE,  CRITICAL_FAILURE, REGISTRY_FAILURE  -> tracer.error(event, result.context());
        };
    }


}
