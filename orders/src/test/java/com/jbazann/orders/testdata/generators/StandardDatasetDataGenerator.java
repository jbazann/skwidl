package com.jbazann.orders.testdata.generators;

import com.jbazann.commons.testing.SeededUUIDGenerator;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.testdata.entities.CustomerMock;
import com.jbazann.orders.testdata.entities.StandardDatasetData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Must be static to initialize enum constants.
public abstract class StandardDatasetDataGenerator {

    // For use wherever a generic String is needed. Also used as random seed because I'm funny.
    private static final String MUSE =
            "The good old days were long gone; " +
            "And now her voice is but a ripple; " +
            "As I dress her pretty, to break the news; " +
            "Oh bittersweet beauty, my life's muse";
    public static final long SEED = MUSE.chars().sum();
    private static final Random random = new Random(SEED);
    private static final SeededUUIDGenerator uuid = new SeededUUIDGenerator(SEED);
    private static final OrderGenerator orderGenerator = new OrderGenerator(random, uuid,
            new DetailGenerator(random, uuid),
            new StatusHistoryGenerator(random, uuid)
    );

    public static List<Order> standardOrderList() {
        return orderGenerator.standardOrderList();
    }

    public static StandardDatasetData orderListFromSameCustomer() {
        final List<Order> orders = orderGenerator.standardOrderListFromSameCustomer();
        return new StandardDatasetData()
                .orders(orders)
                .order(orders.getFirst())
                .customer(CustomerMock.nonNull()
                        .id(orders.getFirst().customer()));
    }

    public static StandardDatasetData newOrderEmptyDetailList() {
        return new StandardDatasetData()
                .order(orderGenerator.generateRandomOrder().detail(new ArrayList<>()));

    }

    public static StandardDatasetData newOrderNoDetail() {
        return new StandardDatasetData()
                .order(orderGenerator.generateRandomOrder().detail(null));

    }

    public static StandardDatasetData newOrderNoSite() {
        return new StandardDatasetData()
                .order(orderGenerator.generateRandomOrder().site(null));

    }

    public static StandardDatasetData newOrderNoUser() {
        return new StandardDatasetData()
                .order(orderGenerator.generateRandomOrder().user(null));
    }

    public static StandardDatasetData newOrderNoCustomer() {
        return new StandardDatasetData()
                .order(orderGenerator.generateRandomOrder().customer(null));
    }

    public static StandardDatasetData newOrderFree() {
        final Order order = orderGenerator.generateFreeOrder();
        return new StandardDatasetData()
                .order(order)
                .customer(CustomerMock.nonNull()
                        .id(order.customer()));
    }

    public static StandardDatasetData newOrderValid() {
        final Order order = orderGenerator.generateRandomOrder();
        final BigDecimal budget = DetailGenerator.getTotalCost(order.detail());
        return new StandardDatasetData()
                .order(order)
                .customer(CustomerMock.nonNull()
                        .id(order.customer())
                        .budget(budget)
                        .budgetResetValue(budget));
    }

    public static StandardDatasetData genericOrder() {
        return new StandardDatasetData()
                .order(orderGenerator.generateRandomOrder());
    }

}
