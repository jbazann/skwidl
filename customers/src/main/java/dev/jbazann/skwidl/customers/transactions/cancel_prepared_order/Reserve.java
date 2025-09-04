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
        value = "CancelPreparedOrderReserve",
        eventClass = CancelPreparedOrderEvent.class,
        stage = Stage.RESERVE
)
public class Reserve implements TransactionStage {

    private final TransactionLifecycleActions transactionActions;

    @Autowired
    public Reserve(TransactionLifecycleActions transactionActions) {
        this.transactionActions = transactionActions;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    // @Transactional
    public TransactionResult runStage(DomainEvent domainEvent,
                                      Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        transaction = transactionActions.reject(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.FAILURE)
                .context("Some failure context.");
    }
}

