package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.commons.async.events.EventRequestPublisher;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import dev.jbazann.skwidl.orders.order.OrderRepository;
import dev.jbazann.skwidl.orders.order.dto.ProductAmountDTO;
import dev.jbazann.skwidl.orders.order.dto.ProductUnitCostDTO;
import dev.jbazann.skwidl.orders.order.dto.StatusUpdateDTO;
import dev.jbazann.skwidl.orders.order.entities.Detail;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.commons.exceptions.MalformedArgumentException;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.identity.KnownMembers;
import dev.jbazann.skwidl.orders.order.dto.NewOrderDTO;
import dev.jbazann.skwidl.orders.order.exceptions.ReserveFailureException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
public class OrderService {

    private final ProductServiceClient productsRemoteService;
    private final CustomerServiceClient customersRemoteService;
    private final OrderNumberServiceLocalClient orderNumberServiceLocalClient;
    private final OrderRepository orderRepository;
    private final DomainEventBuilder builder;
    private final EventRequestPublisher publisher;

    @Autowired
    public OrderService(ProductServiceClient productsRemoteService, CustomerServiceClient customersRemoteService, OrderNumberServiceLocalClient orderNumberServiceLocalClient, OrderRepository orderRepository, DomainEventBuilder builder, EventRequestPublisher publisher) {
        this.productsRemoteService = productsRemoteService;
        this.customersRemoteService = customersRemoteService;
        this.orderNumberServiceLocalClient = orderNumberServiceLocalClient;
        this.orderRepository = orderRepository;
        this.builder = builder;
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
            case IN_PREPARATION -> publisher.request(
                    builder.create()
                            .withQuorumMembers(KnownMembers.memberList(KnownMembers.ORDERS, KnownMembers.CUSTOMERS))//TODO maybe products too
                            .asDeliverOrderEvent(order.id(),order.customer(),order.totalCost()),
                    "Deliver order with id: " + order.id()
            );
            default -> throw new IllegalStateException("Cannot deliver Order in status: " + order.getStatus());
        };
    }

    public void cancelOrder(@NotNull UUID id,
                             @NotNull StatusUpdateDTO update) {
        if( !update.status().equals(StatusHistory.Status.CANCELED) )
            throw new IllegalArgumentException("Expected canceled status but received: " + update.status());

        Order order = getOrder(id);
        switch (order.getStatus().status()) {
            case IN_PREPARATION -> publisher.request(
                    builder.create()
                            .withQuorumMembers(KnownMembers.memberList(KnownMembers.ORDERS, KnownMembers.CUSTOMERS, KnownMembers.PRODUCTS))
                            .asCancelPreparedOrderEvent(
                                    order.id(),
                                    order.customer(),
                                    order.totalCost(),
                                    order.detail().stream()
                                            .collect(Collectors.toMap(Detail::product, Detail::amount))
                    ),
                    "Cancel order with id: " + order.id()
            );
            case ACCEPTED -> publisher.request(
                    builder.create()
                            .withQuorumMembers(KnownMembers.memberList(KnownMembers.ORDERS, KnownMembers.CUSTOMERS))
                            .asCancelAcceptedOrderEvent(
                                    order.id(),
                                    order.customer(),
                                    order.totalCost()
                    ),
                    "Cancel order with id: " + order.id()
            );
            default -> throw new IllegalStateException("Cannot deliver Order in status: " + order.getStatus());
        };
    }

    public @NotNull @Valid Order newOrder(@NotNull NewOrderDTO orderDto) {
        StringBuilder message = validate(orderDto);
        if(!message.isEmpty()) throw new MalformedArgumentException(message.toString());
        Order order = orderDto.toEntity()
                .id(UUID.randomUUID())// TODO safe ids
                .orderNumber(orderNumberServiceLocalClient.next())
                .ordered(TimeProvider.localDateTimeNow())
                .totalCost(BigDecimal.valueOf(-1));
        order.detail().forEach(d -> d.id(UUID.randomUUID()));// TODO safe ids

        // Request validation and unit cost for all the products in the order.
        List<Map<String, Object>> batchToValidate = new LinkedList<>();
        order.detail().forEach(detail -> {
            Map<String, Object> entryToValidate = new HashMap<>();
            entryToValidate.put(ProductServiceHttpClient.PRODUCT_ID, detail.product());
            entryToValidate.put(ProductServiceHttpClient.REQUESTED_STOCK, detail.amount());
            batchToValidate.add(entryToValidate);
        });
        CompletableFuture<Map<String, Object>> detailValidationResponse = productsRemoteService.validateProductAndFetchCost(batchToValidate);

        // Request validation that customer exists, as well as their available budget.
        CompletableFuture<BigDecimal> budgetResponse = customersRemoteService.validateCustomerAndFetchBudget(order.customer());

        // Wait for response from Products service.
        Map<String, Object> validatedBatch = detailValidationResponse.join();
        if ( !(validatedBatch.get(ProductServiceHttpClient.PRODUCTS_EXIST) instanceof final Boolean exist) ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Internal communication error.");
        }
        if( !exist ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Products did not exist.");
        }

        // Double-check the retrieved total cost; because I must justify using CompletableFuture.
            // Type check.
        if( !(validatedBatch.get(ProductServiceHttpClient.TOTAL_COST) instanceof final BigDecimal expectedValue) ) {
            order.totalCost(BigDecimal.valueOf(-1));
            return reject(order,"Internal communication error.");
        }
            // Build the data structures that simplify the calculations.
        Map<UUID, ProductAmountDTO> products = ProductAmountDTO.fromOrder(order);
        Map<UUID, ProductUnitCostDTO> costs = ProductUnitCostDTO.fromValidatedBatch(validatedBatch);
        BigDecimal verifiedCost;
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

        // Attempt to reserve stock.// TODO yuck
        Boolean productsReserved;
        try {
            productsReserved = productsRemoteService.reserveProducts(products);
        } catch (ReserveFailureException e) {
            productsReserved = false;
        }
        if( productsReserved != null && productsReserved ) {
            order = prepare(order, "");
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
    private BigDecimal costsMatch(BigDecimal expected,
                                  Map<UUID, ProductUnitCostDTO> costs,
                                  Map<UUID, ProductAmountDTO> amounts) {
        if( costs.size() != amounts.size() || !costs.keySet().stream().allMatch(amounts::containsKey) )
            return BigDecimal.valueOf(-1);//

        BigDecimal totalCost = costs.keySet().stream()
                // map to cost * amount
                .map(key -> costs.get(key).unitCost().multiply(BigDecimal.valueOf(amounts.get(key).amount())))
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(-1));

        if( totalCost.compareTo(expected) != 0 ) return BigDecimal.valueOf(-1);
        return totalCost;
    }

    private Order reject(Order order, String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID()) // TODO safe ids
                .status(StatusHistory.Status.REJECTED)
                .detail(detail.isEmpty() ? "Rejected." : detail)));
    }

    private Order accept(Order order, String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.ACCEPTED)
                .detail(detail.isEmpty() ? "Accepted." : detail)));
    }

    private Order prepare(Order order, String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.IN_PREPARATION)
                .detail(detail.isEmpty() ? "Products reserved." : detail)));
    }

    private Order deliver(Order order, String detail) {
        return orderRepository.save(order.setStatus(new StatusHistory()
                .id(UUID.randomUUID())// TODO safe ids
                .status(StatusHistory.Status.DELIVERED)
                .detail(detail.isEmpty() ? "Products delivered." : detail)));
    }

    public @NotNull @Valid Order getOrder(@NotNull UUID id) {
        return orderRepository.findOne(Example.of(new Order().id(id))).orElseThrow();
    }

    public @NotNull List<@NotNull @Valid Order> getCustomerOrders(@NotNull UUID customer) {
        return orderRepository.findAll(Example.of(new Order().customer(customer)));
    }

    private StringBuilder validate(NewOrderDTO o) {
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
        } // 8=D
        if(!order.isEmpty()) message.append("The field(s) ").append(order).append("cannot be null.");
        if(o.detail() != null && o.detail().isEmpty()) message.append("The field 'detail' cannot be empty.");
        if(!detail.isEmpty()) message.append("The detail field(s) ").append(detail).append("cannot be null.");
        if(o.detail() != null && o.detail().stream().anyMatch(d -> d.amount() < 0 ))
            message.append("The detail field 'amount' must be a positive integer.");
        return message;
    }

}
