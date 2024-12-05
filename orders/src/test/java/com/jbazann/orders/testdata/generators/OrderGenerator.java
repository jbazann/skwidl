package com.jbazann.orders.testdata.generators;

import com.jbazann.orders.commons.utils.TimeProvider;
import com.jbazann.orders.order.entities.Detail;
import com.jbazann.orders.order.entities.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrderGenerator {

    // Start with a high number as a cheap solution to avoid collisions with application logic.
    private final AtomicLong orderNumberCounter = new AtomicLong(800813500000L);
    private final String MUSE =
            "The good old days were long gone; " +
            "And now her voice is but a ripple; " +
            "As I dress her pretty, to break the news; " +
            "Oh bittersweet beauty, my life's muse";

    private final Random random;
    private final SeededUUIDGenerator uuid;
    private final DetailGenerator detailGenerator;
    private final StatusHistoryGenerator statusHistoryGenerator;

    public OrderGenerator(Random random, SeededUUIDGenerator uuid, DetailGenerator detailGenerator, StatusHistoryGenerator statusHistoryGenerator) {
        this.random = random;
        this.uuid = uuid;
        this.detailGenerator = detailGenerator;
        this.statusHistoryGenerator = statusHistoryGenerator;
    }

    public Order generateRandomOrder() {
        final Long ORDER_NUMBER = orderNumberCounter.getAndIncrement();
        final List<Detail> details = detailGenerator.standardDetailList();
        final BigDecimal TOTAL_COST = details.stream()
                .map(Detail::totalCost).reduce(BigDecimal::add)
                .orElseGet(() -> new BigDecimal(0));
        final Order order = new Order()
                .id(uuid.next())
                .ordered(TimeProvider.localDateTimeNow().minusMinutes(random.nextInt((24 * 60) * 90)))// 90 Days.
                .orderNumber(ORDER_NUMBER)
                .site(uuid.next())
                .customer(uuid.next())
                .user(uuid.next())
                .totalCost(TOTAL_COST)
                .note(MUSE)
                .detail(details);
        return setRandomStatus(order);
    }

    public Order generateFreeOrder() {
        final Long ORDER_NUMBER = orderNumberCounter.getAndIncrement();
        final List<Detail> details = detailGenerator.freeDetailList();
        final BigDecimal TOTAL_COST = new BigDecimal(0); // Ideally freeDetailList guarantees this.
        final Order order = new Order()
                .id(uuid.next())
                .ordered(TimeProvider.localDateTimeNow().minusMinutes(random.nextInt((24 * 60) * 90)))// 90 Days.
                .orderNumber(ORDER_NUMBER)
                .site(uuid.next())
                .customer(uuid.next())
                .user(uuid.next())
                .totalCost(TOTAL_COST)
                .note(MUSE)
                .detail(details);
        return setRandomStatus(order);
    }

    public List<Order> standardOrderList() {
        return standardOrderList(0);
    }

    /**
     * @param AMOUNT_OR_BOUND Positive value returns that exact amount of items in the list,
     *                        negative returns a random amount in the end-excluding range {@code [100; 100 + abs(value)]},
     *                        zero returns a random amount as described, but using a default max bound.
     */
    public List<Order> standardOrderList(final int AMOUNT_OR_BOUND) {
        final int MIN_AMOUNT = 100;
        final int DEFAULT_BOUND = 50;
        final int STD_ORDERS_AMOUNT = AMOUNT_OR_BOUND > 0 ? AMOUNT_OR_BOUND :
                random.nextInt( AMOUNT_OR_BOUND < 0 ? -AMOUNT_OR_BOUND : DEFAULT_BOUND) + MIN_AMOUNT;
        final List<Order> orders = new ArrayList<>(STD_ORDERS_AMOUNT);
        for (int i = 0; i < STD_ORDERS_AMOUNT; i++)
            orders.add(generateRandomOrder());
        return orders;
    }

    public List<Order> standardOrderListFromSameCustomer() {
        return standardOrderListFromSameCustomer(uuid.next(), 10);
    }

    /**
     * @param customer A customer ID.
     * @param AMOUNT_OR_BOUND Positive value returns that exact amount of items in the list,
     *                        negative returns a random amount in the end-excluding range {@code [5; 5 + abs(value)]},
     *                        zero returns a random amount as described, but using a default max bound.
     */
    public List<Order> standardOrderListFromSameCustomer(final UUID customer, final int AMOUNT_OR_BOUND) {
        final int MIN_AMOUNT = 5;
        final int DEFAULT_BOUND = 3;
        final int STD_ORDERS_AMOUNT = AMOUNT_OR_BOUND > 0 ? AMOUNT_OR_BOUND :
                random.nextInt( AMOUNT_OR_BOUND < 0 ? -AMOUNT_OR_BOUND : DEFAULT_BOUND) + MIN_AMOUNT;
        final List<Order> orders = new ArrayList<>(STD_ORDERS_AMOUNT);
        for (int i = 0; i < STD_ORDERS_AMOUNT; i++)
            orders.add(generateRandomOrder()
                    .customer(customer));
        return orders;
    }

    private Order setRandomStatus(Order order) {
        return switch (random.nextInt(5)) {
            case 0 -> statusHistoryGenerator.initRejected(order);
            case 1 -> statusHistoryGenerator.initPreparation(order);
            case 2 -> statusHistoryGenerator.initCanceled(order);
            case 3 -> statusHistoryGenerator.initDelivered(order);
            default -> statusHistoryGenerator.initAccepted(order);
        };
    }

}
