package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.OrderRepository;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
@Service
@Validated
public class OrderLifecycleActions {

    private final OrderRepository orderRepository;

    public OrderLifecycleActions(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> fetch(@NotNull UUID id) {
        return orderRepository.findById(id.toString());
    }

    public @NotNull @Valid Order cancel(@NotNull @Valid Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .setId(UUID.randomUUID())// TODO safe ids
                .setStatus(StatusHistory.Status.CANCELED)
                .setDetail(detail.isEmpty() ? "Order cancelled." : detail)));
    }

    public @NotNull @Valid Order rollbackToAccepted(@NotNull @Valid Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .setId(UUID.randomUUID())// TODO safe ids
                .setStatus(StatusHistory.Status.ACCEPTED)
                .setDetail(detail.isEmpty() ? "Rollback." : detail)));
    }

    public @NotNull @Valid Order deliver(@NotNull @Valid Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .setId(UUID.randomUUID())// TODO safe ids
                .setStatus(StatusHistory.Status.DELIVERED)
                .setDetail(detail.isEmpty() ? "Order delivered." : detail)));
    }

    public @NotNull @Valid Order rollbackToPreparation(@NotNull @Valid Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .setId(UUID.randomUUID())// TODO safe ids
                .setStatus(StatusHistory.Status.IN_PREPARATION)
                .setDetail(detail.isEmpty() ? "Rollback." : detail)));
    }
}
