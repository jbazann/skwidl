package dev.jbazann.skwidl.orders.order.transactions.deliver_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
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
    @Transactional
    public TransactionResult runStage(DomainEvent domainEvent,
                                      Transaction transaction) {
        if (!(domainEvent instanceof DeliverOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        final Optional<Order> OPT = orderActions.fetch(event.getOrderId());
        if (OPT.isEmpty()) {
            transaction = transactionActions.reject(transaction);
            return new TransactionResult()
                    .setData(transaction)
                    .setSimpleResult(TransactionResult.SimpleResult.FAILURE)
                    .setContext("Order not found.");
        }
        final Order order = OPT.get();

        final StatusHistory.Status STATUS = order.getStatusHistory().getLast().getStatus();
        if (STATUS == StatusHistory.Status.DELIVERED) {
            transaction = transactionActions.accept(transaction);
            // TODO single stage transactions are never committed
            return new TransactionResult()
                    .setData(transaction)
                    .setSimpleResult(TransactionResult.SimpleResult.WARNED_SUCCESS)
                    .setContext("Order was already delivered.");
        }

        if(STATUS != StatusHistory.Status.IN_PREPARATION) {
            transaction = transactionActions.reject(transaction);
            return new TransactionResult()
                    .setData(transaction)
                    .setSimpleResult(TransactionResult.SimpleResult.FAILURE)
                    .setContext("Order not prepared.");
        }

        orderActions.deliver(order, "Delivered by transaction id: " + event.getTransaction().getId());
        transactionActions.accept(transaction);
        return new TransactionResult()
                .setData(transaction)
                .setSimpleResult(TransactionResult.SimpleResult.SUCCESS)
                .setContext(String.format(
                        "Order delivered by event %s with context %s",
                        event.getId(), event.getContext()
                ));
    }
}
