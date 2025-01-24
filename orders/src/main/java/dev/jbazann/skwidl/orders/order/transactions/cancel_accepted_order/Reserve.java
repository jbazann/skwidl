package dev.jbazann.skwidl.orders.order.transactions.cancel_accepted_order;

import dev.jbazann.skwidl.commons.async.events.specialized.CancelAcceptedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.Stage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStageBean;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.orders.order.services.OrderLifecycleActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@TransactionStageBean(
        value = "CancelAcceptedOrderReserve",
        eventClass = CancelAcceptedOrderEvent.class,
        stage = Stage.RESERVE
)
public class Reserve implements TransactionStage {

    private final OrderLifecycleActions orderActions;
    private final TransactionLifecycleActions transactionActions;

    @Autowired
    public Reserve(OrderLifecycleActions orderActions, TransactionLifecycleActions transactionActions) {
        this.orderActions = orderActions;
        this.transactionActions = transactionActions;
    }

    @Override
    public List<EntityLock> getRequiredLocks(DomainEvent domainEvent) {
        return TransactionStage.super.getRequiredLocks(domainEvent);
    }

    @Override
    @Transactional
    public TransactionResult runStage(DomainEvent domainEvent, Transaction transaction) {
        if (!(domainEvent instanceof CancelAcceptedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");
        if (transaction == null)
            throw new IllegalArgumentException("Transactions API failed to provide a Transaction instance."); // TODO proper validation

        final Optional<Order> OPT = orderActions.fetch(event.orderId());
        if (OPT.isEmpty()) {
            transaction = transactionActions.reject(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.FAILURE)
                    .context("Order not found.");
        }

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS == StatusHistory.Status.CANCELED) {
            // TODO exceptional transaction status
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.WARNED_SUCCESS)
                    .context("Order was already canceled.");
        }

        if(STATUS != StatusHistory.Status.ACCEPTED) {
            transaction = transactionActions.reject(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.FAILURE)
                    .context("Order status was expected to be 'accepted', but is instead " + STATUS + '.');
        }

        orderActions.cancel(OPT.get(), "Canceled by transaction id: "+event.transaction().id());
        transaction = transactionActions.accept(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("Order gracefully canceled.");
    }
}

