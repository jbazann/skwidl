package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.transactions.TransactionResult.SimpleResult;
import com.jbazann.commons.async.transactions.data.Transaction;

public final class TransactionPhaseExecutor {

    private final TransactionPhaseRegistrar registrar;
    private final TransactionLifecycleActions transactionActions;
    private final TransactionPhase notFoundPhase;
    private final TransactionPhase wrapperReservePhase;
    private final TransactionPhase wrapperCommitPhase;
    private final TransactionPhase wrapperRollbackPhase;

    public TransactionPhaseExecutor(TransactionPhaseRegistrar registrar, TransactionLifecycleActions transactionActions) {
        this.registrar = registrar;
        this.transactionActions = transactionActions;
        notFoundPhase = new NotFoundPhase();
        wrapperReservePhase = new WrapperReservePhase();
        wrapperCommitPhase = new WrapperCommitPhase();
        wrapperRollbackPhase = new WrapperRollbackPhase();
    }

    public TransactionResult runPhaseFor(DomainEvent event) {
        return switch (event.type()) {
            case REQUEST -> wrapperReservePhase.runForEvent(event, null);
            case COMMIT -> wrapperCommitPhase.runForEvent(event, null);
            case ROLLBACK -> wrapperRollbackPhase.runForEvent(event, null);
            default -> throw new IllegalArgumentException(
                    "No transaction phase associated with event type: " + event.type());
        };
    }

    private static final class NotFoundPhase implements TransactionPhase {

        @Override
        public Class<? extends DomainEvent> getEventClass() {
            return DomainEvent.class;
        }

        @Override
        public TransactionResult runForEvent(DomainEvent event, Transaction transaction) {
            return new TransactionResult()
                    .simpleResult(SimpleResult.REGISTRY_FAILURE);
        }

    }
    private final class WrapperReservePhase implements TransactionPhase {


        @Override
        public Class<? extends DomainEvent> getEventClass() {
            return DomainEvent.class;
        }
        @Override
        public TransactionResult runForEvent(DomainEvent event, Transaction transaction) {
            transaction = transactionActions.fetchOrCreateFor(event);

            TransactionResult result = new TransactionResult().data(transaction);
            switch (transaction.status()) {
                case UNKNOWN -> result = registrar.getReservePhaseFor(event).orElse(notFoundPhase)
                        .runForEvent(event, transaction);
                case ACCEPTED, COMMITTED -> result
                        .simpleResult(SimpleResult.WARNED_SUCCESS)
                        .context("The requested transaction has already concluded successfully.");
                case REJECTED -> result
                        .simpleResult(SimpleResult.FAILURE)
                        .context("The requested transaction has already been rejected.");
                case ROLLED_BACK -> result
                        .simpleResult(SimpleResult.FAILURE)
                        .context("The requested transaction has already concluded with a rollback.");
            };
            return result;
        }

    }
    private final class WrapperCommitPhase implements TransactionPhase {

        @Override
        public Class<? extends DomainEvent> getEventClass() {
            return DomainEvent.class;
        }
        @Override
        public TransactionResult runForEvent(DomainEvent event, Transaction transaction) {
            transaction = transactionActions.fetchOrCreateFor(event);

            TransactionResult result = new TransactionResult().data(transaction);
            switch (transaction.status()) {
                case ACCEPTED -> result = registrar.getCommitPhaseFor(event).orElse(notFoundPhase)
                        .runForEvent(event, transaction);
                case COMMITTED -> result
                        .simpleResult(SimpleResult.WARNED_SUCCESS)
                        .context("The transaction has already been committed and concluded successfully.");
                case REJECTED, ROLLED_BACK, UNKNOWN -> result
                        .simpleResult(SimpleResult.CRITICAL_FAILURE)
                        .context("The committed transaction was not accepted.");
            };
            return result;
        }

    }
    private final class WrapperRollbackPhase implements TransactionPhase {

        @Override
        public Class<? extends DomainEvent> getEventClass() {
            return DomainEvent.class;
        }
        @Override
        public TransactionResult runForEvent(DomainEvent event, Transaction transaction) {
            transaction = transactionActions.fetchOrCreateFor(event);

            TransactionResult result = new TransactionResult().data(transaction);
            switch (transaction.status()) {
                case ACCEPTED -> result = registrar.getReservePhaseFor(event).orElse(notFoundPhase)
                        .runForEvent(event, transaction);
                case ROLLED_BACK -> result
                        .simpleResult(SimpleResult.WARNED_SUCCESS)
                        .context("The transaction has already been rolled back.");
                case COMMITTED -> result
                        .simpleResult(SimpleResult.CRITICAL_FAILURE)
                        .context("Cannot rollback a committed transaction.");
                case REJECTED -> result
                        .simpleResult(SimpleResult.WARNED_SUCCESS)
                        .context("The transaction was rejected and did not need a rollback.");
            };
            return result;
        }

    }

}
