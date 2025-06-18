package dev.jbazann.skwidl.orders.testdata.generators;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.testdata.entities.CustomerMock;
import dev.jbazann.skwidl.orders.testdata.entities.StandardDatasetData;

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
                .setOrders(orders)
                .setOrder(orders.getFirst())
                .setCustomer(CustomerMock.nonNull()
                        .setId(orders.getFirst().getCustomer()));
    }

    public static StandardDatasetData newOrderEmptyDetailList() {
        return new StandardDatasetData()
                .setOrder(orderGenerator.generateRandomOrder().setDetail(new ArrayList<>()));

    }

    public static StandardDatasetData newOrderNoDetail() {
        return new StandardDatasetData()
                .setOrder(orderGenerator.generateRandomOrder().setDetail(null));

    }

    public static StandardDatasetData newOrderNoSite() {
        return new StandardDatasetData()
                .setOrder(orderGenerator.generateRandomOrder().setSite(null));

    }

    public static StandardDatasetData newOrderNoUser() {
        return new StandardDatasetData()
                .setOrder(orderGenerator.generateRandomOrder().setUser(null));
    }

    public static StandardDatasetData newOrderNoCustomer() {
        return new StandardDatasetData()
                .setOrder(orderGenerator.generateRandomOrder().setCustomer(null));
    }

    public static StandardDatasetData newOrderFree() {
        final Order order = orderGenerator.generateFreeOrder();
        return new StandardDatasetData()
                .setOrder(order)
                .setCustomer(CustomerMock.nonNull()
                        .setId(order.getCustomer()));
    }

    public static StandardDatasetData newOrderValid() {
        final Order order = orderGenerator.generateRandomOrder();
        final BigDecimal budget = DetailGenerator.getTotalCost(order.getDetail());
        return new StandardDatasetData()
                .setOrder(order)
                .setCustomer(CustomerMock.nonNull()
                        .setId(order.getCustomer())
                        .setBudget(budget)
                        .setBudgetResetValue(budget));
    }

    public static StandardDatasetData genericOrder() {
        return new StandardDatasetData()
                .setOrder(orderGenerator.generateRandomOrder());
    }

}
