package com.jbazann.orders.order;

import com.jbazann.orders.order.dto.NewOrderDTO;
import com.jbazann.orders.order.dto.OrderDTO;
import com.jbazann.orders.order.dto.StatusUpdateDTO;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import com.jbazann.orders.order.services.OrderService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/{id}")
    public OrderDTO getOrder(@NotNull @PathVariable UUID id) {
        return orderService.getOrder(id).toDto();
    }

    @GetMapping("/customer/{id}")
    public List<OrderDTO> getCustomerOrders(@NotNull @PathVariable UUID id) {
        return orderService.getCustomerOrders(id).stream().map(Order::toDto).toList();
    }

    @PostMapping("/order")
    public OrderDTO newOrder(@NotNull @RequestBody NewOrderDTO order) {
        return orderService.newOrder(order).toDto();
    }

    @PutMapping("/order/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrder(@PathVariable UUID id,
                                @NotNull @RequestBody StatusUpdateDTO update) {
        switch(update.status()) {
            case DELIVERED -> orderService.deliverOrder(id, update);
            case CANCELED -> orderService.cancelOrder(id, update);
            default -> throw new IllegalArgumentException("Status must be one of: " +
                        StatusHistory.Status.DELIVERED + ", " + StatusHistory.Status.CANCELED + '.');
        };
    }

}
