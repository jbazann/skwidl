package dev.jbazann.skwidl.orders.order.transactions.cancel_prepared_order;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.CommitPhase;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionPhase;
import dev.jbazann.skwidl.commons.async.transactions.api.implement.Transaction;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.orders.order.services.OrderLifecycleActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@CommitPhase("CancelPreparedOrderCommit")
public class Commit implements TransactionPhase {

    private final OrderLifecycleActions orderActions;
    private final TransactionLifecycleActions transactionActions;

    @Autowired
    public Commit(OrderLifecycleActions orderActions, TransactionLifecycleActions transactionActions) {
        this.orderActions = orderActions;
        this.transactionActions = transactionActions;
    }

    @Override
    public Class<? extends DomainEvent> getEventClass() {
        return CancelPreparedOrderEvent.class;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    // TODO review isolation
    // TODO maybe document annotating this instead of in the interface as per Spring advice
    // https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html
    public TransactionResult runForEvent(DomainEvent domainEvent, Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");
        if (transaction == null)
            throw new IllegalArgumentException("Transactions API failed to provide a Transaction instance."); // TODO proper validation

        Optional<Order> OPT = orderActions.fetch(event.orderId());
        if (OPT.isEmpty()) {
            transaction = transactionActions.error(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order not found.");
        }

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS != StatusHistory.Status.CANCELED) {
            transaction = transactionActions.error(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.CRITICAL_FAILURE)
                    .context("Order status was expected to be 'canceled', but is instead " + STATUS + '.');
        }

        transaction = transactionActions.commit(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("Transaction gracefully committed.");
    }

}