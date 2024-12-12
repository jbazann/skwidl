package com.jbazann.orders.order.services;

import com.jbazann.orders.commons.async.events.CancelAcceptedOrderEvent;
import com.jbazann.orders.commons.async.events.CancelPreparedOrderEvent;
import com.jbazann.orders.commons.async.events.DeliverOrderEvent;
import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.orders.commons.utils.TimeProvider;
import com.jbazann.orders.order.OrderRepository;
import com.jbazann.orders.order.dto.*;
import com.jbazann.orders.order.entities.Detail;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import com.jbazann.orders.order.exceptions.MalformedArgumentException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.jbazann.orders.order.services.ProductsRemoteService.*;

@Service
@Validated
public class SyncOrderService {

    private final ProductsRemoteServiceInterface productsRemoteService;
    private final CustomersRemoteServiceInterface customersRemoteService;
    private final OrderNumberService orderNumberService;
    private final OrderRepository orderRepository;
    private final RabbitPublisher publisher;

    @Autowired
    public SyncOrderService(ProductsRemoteServiceInterface productsRemoteService, CustomersRemoteServiceInterface customersRemoteService, OrderNumberService orderNumberService, OrderRepository orderRepository, RabbitPublisher publisher) {
        this.productsRemoteService = productsRemoteService;
        this.customersRemoteService = customersRemoteService;
        this.orderNumberService = orderNumberService;
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    public boolean orderExists(@NotNull UUID id) {
        return orderRepository.existsById(id.toString());
    }

    public void deliverOrder(@NotNull UUID id,
                              @NotNull StatusUpdateDTO update) {
        if( !update.status().equals(StatusHistory.Status.DELIVERED) )
            throw new IllegalArgumentException("Expected delivered status but received: " + update.status());

        Order order = getOrder(id);
        switch (order.getStatus().status()) {
            case IN_PREPARATION -> {
                DomainEvent event = new DeliverOrderEvent()
                        .orderId(order.id())
                        .customerId(order.customer())
                        .returnedFunds(order.totalCost())
                        .type(DomainEvent.Type.REQUEST)
                        .setRouting();
                publisher.publish(event);
            }
            default -> throw new IllegalStateException("Cannot deliver Order in status: " + order.getStatus());
        };
    }

    public void cancelOrder(@NotNull UUID id,
                             @NotNull StatusUpdateDTO update) {
        if( !update.status().equals(StatusHistory.Status.CANCELED) )
            throw new IllegalArgumentException("Expected canceled status but received: " + update.status());

        Order order = getOrder(id);
        switch (order.getStatus().status()) {
            case IN_PREPARATION -> {
                DomainEvent event = new CancelPreparedOrderEvent()
                        .orderId(order.id())
                        .customerId(order.customer())
                        .returnedFunds(order.totalCost())
                        .returnedStock(order.detail().stream()
                                .collect(Collectors.toMap(Detail::product, Detail::amount)))
                        .type(DomainEvent.Type.REQUEST)
                        .setRouting();
                publisher.publish(event);
            }
            case ACCEPTED -> {
                DomainEvent event = new CancelAcceptedOrderEvent()
                        .orderId(order.id())
                        .customerId(order.customer())
                        .returnedFunds(order.totalCost())
                        .type(DomainEvent.Type.REQUEST)
                        .setRouting();
                publisher.publish(event);
            }
            default -> throw new IllegalStateException("Cannot deliver Order in status: " + order.getStatus());
        };
    }

    public Order newOrder(@NotNull NewOrderDTO orderDto) {
        final StringBuilder message = validate(orderDto);
        if(!message.isEmpty()) throw new MalformedArgumentException(message.toString());
        Order order = orderDto.toEntity()
                .id(UUID.randomUUID())// TODO safe ids
                .orderNumber(orderNumberService.next())
                .ordered(TimeProvider.localDateTimeNow())
                .totalCost(BigDecimal.valueOf(-1));
        order.detail().forEach(d -> d.id(UUID.randomUUID()));// TODO safe ids

        // Request validation and unit cost for all the products in the order.
        final List<Map<String, Object>> batchToValidate = new LinkedList<>();
        order.detail().forEach(detail -> {
            Map<String, Object> entryToValidate = new HashMap<>();
            entryToValidate.put(PRODUCT_ID, detail.product());
            entryToValidate.put(REQUESTED_STOCK, detail.amount());
            batchToValidate.add(entryToValidate);
        });
        final CompletableFuture<Map<String, Object>> detailValidationResponse = productsRemoteService.validateProductAndFetchCost(batchToValidate);

        // Request validation that customer exists, as well as their available budget.
        final CompletableFuture<BigDecimal> budgetResponse = customersRemoteService.validateCustomerAndFetchBudget(order.customer());

        // Wait for response from Products service.
        final Map<String, Object> validatedBatch = detailValidationResponse.join();
        if ( !(validatedBatch.get(PRODUCTS_EXIST) instanceof final Boolean exist) ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Internal communication error.");
        }
        if( !exist ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Products did not exist.");
        }

        // Parse product availability, only necessary because I deserialized into a map instead of a proper object.
        if ( !(validatedBatch.get(STOCK_AVAILABLE) instanceof final Boolean stockAvailable) ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Internal communication error.");
        }

        // Double-check the retrieved total cost; because I must justify using CompletableFuture.
            // Type check.
        if( !(validatedBatch.get(TOTAL_COST) instanceof final BigDecimal expectedValue) ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Internal communication error.");
        }
            // Build the data structures that simplify the calculations.
        final Map<UUID, ProductAmountDTO> products = ProductAmountDTO.fromOrder(order);
        final Map<UUID, ProductUnitCostDTO> costs = ProductUnitCostDTO.fromValidatedBatch(validatedBatch);
        final BigDecimal verifiedCost;
            // Verify, and add the correct value to the order.
        if( (verifiedCost = costsMatch(expectedValue, costs, products) )
                .compareTo(BigDecimal.valueOf(-1)) == 0 ) {// if (verifiedCost == -1).
            //TODO exceptions
        }
        order.totalCost(verifiedCost);

        // Check that budget is enough.
        BigDecimal budget = budgetResponse.join();
        if( budget.compareTo(verifiedCost) < 0 ) {
            return reject(order, "Insufficient funds.");
        }

        // Reserve customer funds.
        Boolean success = customersRemoteService.billFor(order.customer(),order.totalCost());
        if ( success != null && success ) {
            order = accept(order,"");
        } else {
            return reject(order, success == null ? "Null billing response." : "Insufficient funds.");
        }

        // Reserve stock, if available.
        if( stockAvailable ) {
            final Boolean productsReserved = productsRemoteService.reserveProducts(products);
            if( productsReserved != null && productsReserved ) {
                order = prepare(order, "");
            }
        }

        return order;
    }

    /**
     * Verifies that the products contained in the response from the Products service are
     * the requested ones. Then calculates the total cost using the received unit costs
     * and the known requested amounts, and finally compares this value to the expected
     * total cost provided by the Products service.
     * @param expected the cost calculated by the Products service.
     * @param costs the unit costs retrieved from the Products service.
     * @param amounts the amounts requested in the {@link Order}
     * @return the total cost of all products, or -1 if the provided data is inconsistent.
     */
    private BigDecimal costsMatch(@NotNull BigDecimal expected,
                                  @NotNull Map<UUID, ProductUnitCostDTO> costs,
                                  @NotNull Map<UUID, ProductAmountDTO> amounts) {
        if( costs.size() != amounts.size() || !costs.keySet().stream().allMatch(amounts::containsKey) )
            return BigDecimal.valueOf(-1);//

        BigDecimal totalCost = costs.keySet().stream()
                // map to cost * amount
                .map(key -> costs.get(key).unitCost().multiply(BigDecimal.valueOf(amounts.get(key).amount())))
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(-1));

        if( totalCost.compareTo(expected) != 0 ) return BigDecimal.valueOf(-1);
        return totalCost;
    }

    private Order reject(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID()) // TODO safe ids
                .status(StatusHistory.Status.REJECTED)
                .detail(detail.isEmpty() ? "Rejected." : detail)));
    }

    private Order accept(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.ACCEPTED)
                .detail(detail.isEmpty() ? "Accepted." : detail)));
    }

    private Order prepare(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.IN_PREPARATION)
                .detail(detail.isEmpty() ? "Products reserved." : detail)));
    }

    private Order deliver(@NotNull Order order, @NotNull String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.DELIVERED)
                .detail(detail.isEmpty() ? "Products delivered." : detail)));
    }

    public Order getOrder(@NotNull UUID id) {
        return orderRepository.findOne(Example.of(new Order().id(id))).orElseThrow();
    }

    public List<Order> getCustomerOrders(UUID customer) {
        return orderRepository.findAll(Example.of(new Order().customer(customer)));
    }

    public StringBuilder validate(@NotNull NewOrderDTO o) {
        final StringBuilder order = new StringBuilder();
        final StringBuilder detail = new StringBuilder();
        final StringBuilder message = new StringBuilder();
        if( o.customer() == null ) order.append("'customer', ");
        if( o.user() == null ) order.append("'user', ");
        if( o.site() == null ) order.append("'site', ");
        if( o.detail() == null ) order.append("'detail', ");
        if (o.detail() != null && !o.detail().isEmpty()) {
            if(o.detail().stream().anyMatch(d -> d.amount() == null)) detail.append("'amount', ");
            if(o.detail().stream().anyMatch(d -> d.product() == null)) detail.append("'product', ");
        }
        if(!order.isEmpty()) message.append("The field(s) ").append(order).append("cannot be null.");
        if(o.detail() != null && o.detail().isEmpty()) message.append("The field 'detail' cannot be empty.");
        if(!detail.isEmpty()) message.append("The detail field(s) ").append(detail).append("cannot be null.");
        if(o.detail() != null && o.detail().stream().anyMatch(d -> d.amount() < 0 ))
            message.append("The detail field 'amount' must be a positive integer.");
        return message;
    }

}
