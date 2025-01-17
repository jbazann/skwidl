package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.OrderRepository;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderLifecycleActions {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderLifecycleActions(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> fetch(UUID id) {
        return orderRepository.findById(id.toString());
    }

    public Order cancel(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.CANCELED)
                .detail(detail.isEmpty() ? "Order cancelled." : detail)));
    }

    public  Order rollbackToAccepted(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.ACCEPTED)
                .detail(detail.isEmpty() ? "Rollback." : detail)));
    }
}
