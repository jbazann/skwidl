package dev.jbazann.skwidl.orders.testdata.generators;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.orders.order.entities.Detail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DetailGenerator {

    private final Random random;
    private final SeededUUIDGenerator uuid;

    public DetailGenerator(Random random, SeededUUIDGenerator uuid) {
        this.random = random;
        this.uuid = uuid;
    }

    public static BigDecimal getTotalCost(List<Detail> detail) {
        return detail.stream()
                .map(Detail::totalCost)
                .reduce(BigDecimal::add)
                .orElseGet(() -> new BigDecimal(0));
    }

    public List<Detail> standardDetailList() {
        return standardDetailList(0);
    }

    /**
     * @param AMOUNT_OR_BOUND Positive value returns that exact amount of items in the list,
     *                        negative returns a random amount in the end-excluding range {@code [1; 1 + abs(value)]},
     *                        zero returns a random amount as described, but using a default max bound.
     */
    public List<Detail> standardDetailList(final int AMOUNT_OR_BOUND) {
        final int MIN_AMOUNT = 1;
        final int DEFAULT_BOUND = 12;
        final int STD_DETAILS_AMOUNT = AMOUNT_OR_BOUND > 0 ? AMOUNT_OR_BOUND :
                random.nextInt(AMOUNT_OR_BOUND < 0 ? MIN_AMOUNT - AMOUNT_OR_BOUND : MIN_AMOUNT + DEFAULT_BOUND) + MIN_AMOUNT;
        final List<Detail> details = new ArrayList<>(STD_DETAILS_AMOUNT);
        for (int i = 0; i < STD_DETAILS_AMOUNT; i++)
            details.add(generateRandomDetail(-1, -1, -1));
        return details;
    }

    public List<Detail> freeDetailList() {
        return freeDetailList(0);
    }

    /**
     * @param AMOUNT_OR_BOUND Positive value returns that exact amount of items in the list,
     *                        negative returns a random amount in the end-excluding range {@code [1; 1 + abs(value)]},
     *                        zero returns a random amount as described, but using a default max bound.
     */
    public List<Detail> freeDetailList(final int AMOUNT_OR_BOUND) {
        final int FULL_DISCOUNT = 100;
        final int MIN_AMOUNT = 1;
        final int DEFAULT_BOUND = 12;
        final int STD_DETAILS_AMOUNT = AMOUNT_OR_BOUND > 0 ? AMOUNT_OR_BOUND :
                random.nextInt(AMOUNT_OR_BOUND < 0 ? MIN_AMOUNT - AMOUNT_OR_BOUND : MIN_AMOUNT + DEFAULT_BOUND) + MIN_AMOUNT;
        final List<Detail> details = new ArrayList<>(STD_DETAILS_AMOUNT);
        for (int i = 0; i < STD_DETAILS_AMOUNT; i++)
            details.add(generateRandomDetail(-1, -1, FULL_DISCOUNT));
        return details;
    }

    private Detail generateRandomDetail(int UNIT_COST, int UNITS, int DISCOUNT_PERCENT) {
        if (UNIT_COST < 1) UNIT_COST = random.nextInt(101);
        if (UNITS < 1) UNITS = random.nextInt(6);
        if (DISCOUNT_PERCENT < 0) DISCOUNT_PERCENT = random.nextInt(101);
        return new Detail()
                .id(uuid.next())
                .product(uuid.next())
                .amount(UNITS)
                .discount(BigDecimal.valueOf((long) UNITS * UNIT_COST * DISCOUNT_PERCENT, 2))
                .unitCost(new BigDecimal(UNIT_COST))
                .totalCost(new BigDecimal(UNIT_COST * UNITS));
    }


}
