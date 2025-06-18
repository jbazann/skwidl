package dev.jbazann.skwidl.orders.order;

import dev.jbazann.skwidl.orders.order.dto.NewOrderDTO;
import dev.jbazann.skwidl.orders.order.dto.OrderDTO;
import dev.jbazann.skwidl.orders.order.dto.StatusUpdateDTO;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.commons.exceptions.BadRequestException;
import dev.jbazann.skwidl.orders.order.exceptions.CustomerNotFoundException;
import dev.jbazann.skwidl.commons.exceptions.MalformedArgumentException;
import dev.jbazann.skwidl.orders.order.exceptions.OrderNotFoundException;
import dev.jbazann.skwidl.orders.order.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable UUID id) {
        return orderService.getOrder(id).toDto();
    }

    @GetMapping("/customer/{id}")
    public List<OrderDTO> getCustomerOrders(@PathVariable UUID id) {
        return orderService.getCustomerOrders(id).stream().map(Order::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO newOrder(@RequestBody NewOrderDTO order) {
        return orderService.newOrder(order).toDto();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrder(@PathVariable UUID id,
                                @RequestBody StatusUpdateDTO update) {
        if (!orderService.orderExists(id)) throw new OrderNotFoundException("No Order found with id: " + id +'.');
        switch(update.getStatus()) {
            case StatusHistory.Status.DELIVERED -> orderService.deliverOrder(id, update);
            case StatusHistory.Status.CANCELED -> orderService.cancelOrder(id, update);
            default -> throw new BadRequestException("Status must be one of: " +
                        StatusHistory.Status.DELIVERED + ", " + StatusHistory.Status.CANCELED + '.');
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String malformedArgumentException(MalformedArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestException(BadRequestException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String customerNotFoundException(CustomerNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String orderNotFoundException(OrderNotFoundException exception) {
        return exception.getMessage();
    }

}
