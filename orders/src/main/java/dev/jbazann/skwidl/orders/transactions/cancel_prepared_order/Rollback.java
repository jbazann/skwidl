package dev.jbazann.skwidl.orders.transactions.cancel_prepared_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.Stage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStageBean;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.orders.order.services.OrderLifecycleActions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@TransactionStageBean(
        value = "CancelPreparedOrderRollback",
        eventClass = CancelPreparedOrderEvent.class,
        stage = Stage.ROLLBACK
)
public class Rollback implements TransactionStage {

    private final OrderLifecycleActions orderActions;
    private final TransactionLifecycleActions transactionActions;

    @Autowired
    public Rollback(OrderLifecycleActions orderActions, TransactionLifecycleActions transactionActions) {
        this.orderActions = orderActions;
        this.transactionActions = transactionActions;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    // @Transactional
    public TransactionResult runStage(DomainEvent domainEvent,
                                      Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        Optional<Order> OPT = orderActions.fetch(event.orderId());
        if (OPT.isEmpty()) {
            transactionActions.error(transaction);
            return new TransactionResult()
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order not found.");
        }
        final Order order = OPT.get();

        final StatusHistory.Status STATUS = order.statusHistory().getLast().status();
        if (STATUS != StatusHistory.Status.CANCELED) {
            transactionActions.error(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order status was expected to be 'canceled', but is instead " + STATUS + '.');
        }

        orderActions.rollbackToPreparation(order, "Failed to cancel with event context: " + event.context());
        transactionActions.rollback(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("TransactionEntity gracefully rolled back.");

    }
}

