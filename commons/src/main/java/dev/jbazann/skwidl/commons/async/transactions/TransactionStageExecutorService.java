package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class TransactionStageExecutorService {

    private final TransactionStageRegistrarService registrar;
    private final TransactionLifecycleActions transactionActions;
    private final TransactionLockingService lockingService;
    private final Logger log = LoggerFactory.get(TransactionStageExecutorService.class);

    public TransactionStageExecutorService(TransactionStageRegistrarService registrar, TransactionLifecycleActions transactionActions, TransactionLockingService lockingService) {
        this.registrar = registrar;
        this.transactionActions = transactionActions;
        this.lockingService = lockingService;
    }

    public @NotNull @Valid TransactionResult runStageFor(@NotNull @Valid DomainEvent event) {
        log.method(event);
        TransactionStage stage = registrar.getStageForEvent(event);
        log.debug("Retrieved stage: {}", stage);
        Transaction transaction = transactionActions.fetchOrCreateForEvent(event);
        log.debug("Retrieved transaction {}", transaction);
        lockingService.getLocks(stage, event);
        TransactionResult result = stage.runStage(event, transaction);
        lockingService.releaseLocks(stage, event);
        result.data(transaction);
        log.result(result);
        return result;
    }

}
