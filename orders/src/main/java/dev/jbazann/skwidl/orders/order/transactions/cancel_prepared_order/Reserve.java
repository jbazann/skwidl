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
        value = "CancelPreparedOrderReserve",
        eventClass = CancelPreparedOrderEvent.class,
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

    @SuppressWarnings("DuplicatedCode")
    @Override
    // @Transactional
    public TransactionResult runStage(DomainEvent domainEvent,
                                      Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        final Optional<Order> OPT = orderActions.fetch(event.orderId());
        if (OPT.isEmpty()) {
            transaction = transactionActions.reject(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.FAILURE)
                    .context("Order not found.");
        }
        final Order order = OPT.get();

        final StatusHistory.Status STATUS = order.statusHistory().getLast().status();
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

        // TODO transaction.
        orderActions.cancel(order, "Canceled by transaction id: "+event.transaction().id());
        transaction = transactionActions.accept(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("Order gracefully canceled.");

    }
}

