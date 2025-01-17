package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.DeliverOrderEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.orders.order.OrderRepository;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// TODO better creational pattern for events
@Service
public class AsyncOrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public AsyncOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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

}
