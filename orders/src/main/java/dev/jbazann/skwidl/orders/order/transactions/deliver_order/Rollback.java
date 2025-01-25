package dev.jbazann.skwidl.orders.order.transactions.deliver_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.Stage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStageBean;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.orders.order.services.OrderLifecycleActions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@TransactionStageBean(
        value = "DeliverOrderRollback",
        eventClass = DeliverOrderEvent.class,
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


    @Override
    public List<EntityLock> getRequiredLocks(@NotNull @Valid DomainEvent domainEvent) {
        return TransactionStage.super.getRequiredLocks(domainEvent);
    }

    @Override
    @Transactional
    public TransactionResult runStage(@NotNull @Valid DomainEvent domainEvent,
                                      @NotNull @Valid Transaction transaction) {
        if (!(domainEvent instanceof DeliverOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        final Optional<Order> OPT = orderActions.fetch(event.orderId());
        if (OPT.isEmpty()) {
            transaction = transactionActions.error(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order not found.");
        }

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS != StatusHistory.Status.DELIVERED) {
            transaction = transactionActions.error(transaction);
            // TODO single stage transactions are never committed
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order status was expected to be 'delivered', but is instead " + STATUS + '.');
        }

        orderActions.rollbackToPreparation(OPT.get(), "Failed to deliver with context " + event.context());
        transactionActions.rollback(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context(String.format(
                        "Order delivered by event %s with context %s",
                        event.id(), event.context()
                ));
    }
}
