package dev.jbazann.skwidl.customers.transactions.cancel_prepared_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.Stage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStageBean;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

@TransactionStageBean(
        value = "CancelPreparedOrderRollback",
        eventClass = CancelPreparedOrderEvent.class,
        stage = Stage.ROLLBACK
)
public class Rollback implements TransactionStage {

    private final TransactionLifecycleActions transactionActions;

    @Autowired
    public Rollback(TransactionLifecycleActions transactionActions) {
        this.transactionActions = transactionActions;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    // @Transactional
    public TransactionResult runStage(DomainEvent domainEvent,
                                      Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        transactionActions.rollback(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("Transaction gracefully rolled back.");

    }
}

