package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;

import java.util.Optional;

public final class TransactionStageExecutorService {

    private final TransactionStageRegistrarService registrar;
    private final TransactionLifecycleActions transactionActions;
    private final TransactionLockingService lockingService;

    public TransactionStageExecutorService(TransactionStageRegistrarService registrar, TransactionLifecycleActions transactionActions, TransactionLockingService lockingService) {
        this.registrar = registrar;
        this.transactionActions = transactionActions;
        this.lockingService = lockingService;
    }

    public TransactionResult runPhaseFor(DomainEvent event) {
        Optional<TransactionStage> opt = registrar.getStageForEvent(event);
        if (opt.isEmpty()) throw new IllegalStateException(
                String.format("No TransactionStage registered for event ID %s of type %s.", event.id(), event.type()));
        TransactionStage stage = opt.get();
        Transaction transaction = transactionActions.fetchOrCreateFor(event);
        lockingService.getLocks(stage, event);
        TransactionResult result = stage.runStage(event, transaction);
        lockingService.releaseLocks(stage, event);
        result.data(transaction);
        return result;
    }

}
