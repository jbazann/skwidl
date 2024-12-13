package com.jbazann.orders.order.services;

import com.jbazann.commons.async.events.CancelAcceptedOrderEvent;
import com.jbazann.commons.async.events.CancelPreparedOrderEvent;
import com.jbazann.commons.async.events.DeliverOrderEvent;
import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.orchestration.DomainEventProcessorService;
import com.jbazann.commons.async.events.*;
import com.jbazann.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.orders.order.OrderRepository;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.jbazann.commons.async.events.DomainEvent.Type.*;

// TODO better creational pattern for events
@Service
public class AsyncOrderService {

    private final RabbitPublisher publisher;
    private final OrderRepository orderRepository;
    private final DomainEventProcessorService standardEventProcessor;

    @Autowired
    public AsyncOrderService(RabbitPublisher publisher, OrderRepository orderRepository) {
        this.publisher = publisher;
        this.orderRepository = orderRepository;
        this.standardEventProcessor = new DomainEventProcessorService(publisher, ORDERS);
    }

    public void cancelAcceptedOrder(CancelAcceptedOrderEvent event) {
        if(standardEventProcessor.canHandle(event)) return;
        if(event.type() == REJECT) {
            standardEventProcessor.discardEvent(event, "Operation rejected. No action required.");
            return;
        }

        final Optional<Order> OPT = orderRepository.findById(event.orderId().toString());
        if (OPT.isEmpty()) {
            if (event.type() == COMMIT) {
                // This will never happen, I promise.
                standardEventProcessor.hitTheWoah(event);
                return;
            }
            if (event.type() == REQUEST) {
                final DomainEvent response = CancelAcceptedOrderEvent.copyOf(event)
                        .context("Order not found.")
                        .type(REJECT)
                        .setRouting();
                publisher.publish(response);
                return;
            }
            standardEventProcessor.discardEvent(event);
            return;
        }

        /*
         * ### BEGIN CHECKING IF THE OPERATION IS POSSIBLE ###
         */

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS == StatusHistory.Status.CANCELED && event.type() == REQUEST) {
            final DomainEvent response = CancelAcceptedOrderEvent.copyOf(event)
                    .context("Order was already canceled.")
                    .type(REJECT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        if(STATUS != StatusHistory.Status.ACCEPTED && event.type() == REQUEST) {
            final DomainEvent response = CancelAcceptedOrderEvent.copyOf(event)
                    .context("Order status was expected to be 'accepted', but is instead " + STATUS + '.')
                    .type(DomainEvent.Type.REJECT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        /*
         * ### END CHECKING IF THE OPERATION IS POSSIBLE ###
         */ // TODO bad comments

        if(event.type() == REQUEST) {
            final DomainEvent response = CancelAcceptedOrderEvent.copyOf(event)
                    .context("The operation is possible.")
                    .transactionQuorum(event.transactionQuorum().setReady(ORDERS))
                    .type(ACCEPT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        if (event.type() == ACCEPT && !event.transactionQuorum().isReady(ORDERS)) {
            standardEventProcessor.fixOutdatedQuorum(CancelAcceptedOrderEvent.copyOf(event));
            return;
        }

        if (event.type() == COMMIT) {
            final String DETAIL = "Order canceled by event " + event.id() +" with context '" + event.context() + "'.";
            if (STATUS == StatusHistory.Status.CANCELED) {
                /*
                 * Because a REJECT event is published above when STATUS is found to be CANCELED on a REQUEST event,
                 * this can either be a duplicate commit, a retry (from the double write below),
                 * or a duplicate operation (caused by duplicate, concurrent requests).
                 * I will assume the first or second case, and we will pretend the third scenario is
                 * covered and that we live in a happy reality.
                 * TODO this has no implications for this service, but it may be very bad for the others, review.
                 */
                final DomainEvent response = CancelAcceptedOrderEvent.copyOf(event)
                        .context("A commit was processed, but the operation had already been performed. " +
                                "Found order status detail: "+ OPT.get().getStatus().detail())
                        .type(DISCARD)// TODO
                        .setRouting();
                publisher.publish(response);
                return;
            }

            final DomainEvent response = CancelAcceptedOrderEvent.copyOf(event)
                    .context("Order gracefully canceled.")
                    .type(ACK)
                    .setRouting();

            // TODO idempotency
            // Note the double write
            cancel(OPT.get(), DETAIL);
            publisher.publish(response);
            return;
        }

        throw new IllegalArgumentException()

    }

    public void cancelPreparedOrder(CancelPreparedOrderEvent event) {
        if(event.type() == Type.ROLLBACK) return; // TODO action required.
        if(event.type() != Type.REQUEST) return; // TODO review scenarios.

        final Optional<Order> OPT = orderRepository.findById(event.orderId().toString());
        if (OPT.isEmpty()) {
            final DomainEvent response = CancelPreparedOrderEvent.from(event)
                    .context("Order not found.")
                    .type(Type.REJECT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS == StatusHistory.Status.CANCELED) {
            // This is either a duplicate request or the original was not gracefully consumed.
            // Publishing success is idempotent and required, so do that.
            final DomainEvent response = CancelPreparedOrderEvent.from(event)
                    .context("Order was already canceled. Possible duplicate or resumed operation.")
                    .type(Type.ACCEPT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        if(STATUS != StatusHistory.Status.IN_PREPARATION) {
            final DomainEvent response = CancelPreparedOrderEvent.from(event)
                    .context("Order status was (unexpectedly) not 'in preparation' nor 'canceled'.")
                    .type(Type.REJECT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        final String DETAIL = "Order canceled by event " + event.id() +" with context '" + event.context() + "'.";
        final Order order = cancel(OPT.get(), DETAIL);
        final DomainEvent response = CancelPreparedOrderEvent.from(event)
                .context("Order gracefully canceled.")
                .type(Type.ACCEPT)
                .setRouting();
        // Note double write.
        orderRepository.save(order);
        publisher.publish(response);
    }

    public void deliverOrder(DeliverOrderEvent event) {
        if(event.type() == Type.ROLLBACK) return; // TODO action required.
        if(event.type() != Type.REQUEST) return; // TODO review scenarios.

        final Optional<Order> OPT = orderRepository.findById(event.orderId().toString());
        if (OPT.isEmpty()) {
            final DomainEvent response = DeliverOrderEvent.from(event)
                    .context("Order not found.")
                    .type(Type.REJECT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        final StatusHistory.Status STATUS = OPT.get().statusHistory().getLast().status();
        if (STATUS == StatusHistory.Status.DELIVERED) {
            // This is either a duplicate request or the original was not gracefully consumed.
            // Publishing success is idempotent and required, so do that.
            final DomainEvent response = DeliverOrderEvent.from(event)
                    .context("Order was already delivered. Possible duplicate or resumed operation.")
                    .type(Type.ACCEPT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        if(STATUS != StatusHistory.Status.IN_PREPARATION) {
            final DomainEvent response = DeliverOrderEvent.from(event)
                    .context("Order status was (unexpectedly) not 'in preparation' nor 'canceled'.")
                    .type(Type.REJECT)
                    .setRouting();
            publisher.publish(response);
            return;
        }

        final String DETAIL = "Order canceled by event " + event.id() +" with context '" + event.context() + "'.";
        final Order order = cancel(OPT.get(), DETAIL);
        final DomainEvent response = DeliverOrderEvent.from(event)
                .context("Order gracefully canceled.")
                .type(Type.ACCEPT)
                .setRouting();
        // Note double write.
        orderRepository.save(order);
        publisher.publish(response);
    }

    private Order cancel(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.CANCELED)
                .detail(detail.isEmpty() ? "Order cancelled." : detail)));
    }

}
