package dev.jbazann.skwidl.orders.transactions.deliver_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
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
        value = "DeliverOrderReserve",
        eventClass = DeliverOrderEvent.class,
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
        if (!(domainEvent instanceof DeliverOrderEvent event))
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
        if (STATUS == StatusHistory.Status.DELIVERED) {
            transaction = transactionActions.accept(transaction);
            // TODO single stage transactions are never committed
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.WARNED_SUCCESS)
                    .context("Order was already delivered.");
        }

        if(STATUS != StatusHistory.Status.IN_PREPARATION) {
            transaction = transactionActions.reject(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.FAILURE)
                    .context("Order not prepared.");
        }

        orderActions.deliver(order, "Delivered by transaction id: " + event.transaction().id());
        transactionActions.accept(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context(String.format(
                        "Order delivered by event %s with context %s",
                        event.id(), event.context()
                ));
    }
}
