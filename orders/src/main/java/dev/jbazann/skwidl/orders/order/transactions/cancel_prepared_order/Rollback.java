package dev.jbazann.skwidl.orders.order.transactions.cancel_prepared_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.Stage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStageBean;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.orders.order.services.OrderLifecycleActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public TransactionResult runStage(DomainEvent domainEvent,
                                      Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        Optional<Order> OPT = orderActions.fetch(event.getOrderId());
        if (OPT.isEmpty()) {
            transactionActions.error(transaction);
            return new TransactionResult()
                    .setData(transaction)
                    .setSimpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .setContext("Order not found.");
        }
        final Order order = OPT.get();

        final StatusHistory.Status STATUS = order.getStatusHistory().getLast().getStatus();
        if (STATUS != StatusHistory.Status.CANCELED) {
            transactionActions.error(transaction);
            return new TransactionResult()
                    .setData(transaction)
                    .setSimpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .setContext("Order status was expected to be 'canceled', but is instead " + STATUS + '.');
        }

        orderActions.rollbackToAccepted(order, "Failed to cancel with event context: " + event.getContext());
        transactionActions.rollback(transaction);
        return new TransactionResult()
                .setData(transaction)
                .setSimpleResult(TransactionResult.SimpleResult.SUCCESS)
                .setContext("Transaction gracefully rolled back.");

    }
}

