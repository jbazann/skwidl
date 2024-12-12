package com.jbazann.orders.commons.async.orchestration;

import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.async.events.DomainEventTracer;
import com.jbazann.orders.commons.async.transactions.TransactionResult;

public class TransactionResponseProvider {

    private final DomainEventTracer tracer;

    public TransactionResponseProvider(DomainEventTracer tracer) {
        this.tracer = tracer;
    }

    public DomainEvent getResponse(DomainEvent event, TransactionResult result) {
        return switch (event.type()) {
            case ROLLBACK -> responseForRollback(event, result);
            case COMMIT -> responseForCommit(event, result);
            case REQUEST -> responseForRequest(event, result);
            default -> throw new IllegalArgumentException(
                    "Attempted to evaluate TransactionResult for a non-transactional event.");
        };
    }

    private DomainEvent responseForRequest(DomainEvent event, TransactionResult result) {
        return switch (result.simpleResult()) {
            case SUCCESS -> tracer.accept(event, result.context());
            case FAILURE, CRITICAL_FAILURE, REGISTRY_FAILURE -> tracer.reject(event, result.context());
            // TODO CRITICAL_FAILURE should result in error & reject
        };
    }

    private DomainEvent responseForCommit(DomainEvent event, TransactionResult result) {
        /*
         * Ack regardless of result because at this point rolling back is not
         * possible.
         * // TODO evaluate recovery measures for errors during commit phase.
         */
        return tracer.acknowledge(event, result.context());
    }

    private DomainEvent responseForRollback(DomainEvent event, TransactionResult result) {
        return switch (result.simpleResult()) {
            case SUCCESS -> tracer.acknowledge(event, result.context());
            case FAILURE,  CRITICAL_FAILURE, REGISTRY_FAILURE  -> tracer.error(event, result.context());
        };
    }


}
