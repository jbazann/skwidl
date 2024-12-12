package com.jbazann.orders.commons.async.transactions;

import com.jbazann.orders.commons.async.events.DomainEvent;
public final class TransactionPhaseExecutor {

    private final TransactionPhaseRegistrar registrar;

    public TransactionPhaseExecutor(TransactionPhaseRegistrar registrar) {
        this.registrar = registrar;
    }

    public TransactionResult run(DomainEvent event) {
        return switch (event.type()) {
            case REQUEST -> registrar.getReservePhaseFor(event).orElse(NotFound.staticSingleton)
                    .runForEvent(event);
            case COMMIT -> registrar.getCommitPhaseFor(event).orElse(NotFound.staticSingleton)
                    .runForEvent(event);
            case ROLLBACK -> registrar.getRollbackPhaseFor(event).orElse(NotFound.staticSingleton)
                    .runForEvent(event);
            default -> throw new IllegalArgumentException(
                    "No transaction phase associated with event type: " + event.type());
        };
    }

    private static final class NotFound implements TransactionPhase {

        private static final NotFound staticSingleton = new NotFound();

        @Override
        public Class<? extends DomainEvent> getEventClass() {
            return DomainEvent.class;
        }

        @Override
        public TransactionResult runForEvent(DomainEvent event) {
            return new TransactionResult()
                    .simpleResult(TransactionResult.SimpleResult.NOT_FOUND);
        }

    }

}
