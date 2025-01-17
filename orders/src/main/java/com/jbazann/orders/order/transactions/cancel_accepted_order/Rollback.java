package com.jbazann.orders.order.transactions.cancel_accepted_order;

import com.jbazann.commons.async.events.specialized.CancelAcceptedOrderEvent;
import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.transactions.api.RollbackPhase;
import com.jbazann.commons.async.transactions.api.TransactionLifecycleActions;
import com.jbazann.commons.async.transactions.api.TransactionPhase;
import com.jbazann.commons.async.transactions.TransactionResult;
import com.jbazann.commons.async.transactions.api.implement.Transaction;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import com.jbazann.orders.order.services.OrderLifecycleActions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RollbackPhase("CancelAcceptedOrderRollback")
public class Rollback implements TransactionPhase {

    private final OrderLifecycleActions orderActions;
    private final TransactionLifecycleActions transactionActions;

    @Autowired
    public Rollback(OrderLifecycleActions orderActions, TransactionLifecycleActions transactionActions) {
        this.orderActions = orderActions;
        this.transactionActions = transactionActions;
    }

    @Override
    public Class<? extends DomainEvent> getEventClass() {
        return CancelAcceptedOrderEvent.class;
    }

    @Override
    public TransactionResult runForEvent(DomainEvent domainEvent, Transaction transaction) {
        if (!(domainEvent instanceof CancelAcceptedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");
        if (transaction == null)
            throw new IllegalArgumentException("Transactions API failed to provide a Transaction instance."); // TODO proper validation

        Optional<Order> OPT = orderActions.fetch(event.orderId());
        if (OPT.isEmpty()) {
            transactionActions.error(transaction);
            return new TransactionResult()
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order not found.");
        }

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS != StatusHistory.Status.CANCELED) {
            transactionActions.error(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order status was expected to be 'canceled', but is instead " + STATUS + '.');
        }

        orderActions.rollbackToAccepted(OPT.get(), "Failed to cancel with event context: " + event.context());
        transactionActions.rollback(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("Transaction gracefully committed.");
    }

}

