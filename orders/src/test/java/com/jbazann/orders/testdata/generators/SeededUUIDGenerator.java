package com.jbazann.orders.testdata.generators;

import java.util.Random;
import java.util.UUID;

/**
 * Basic utility class to simplify seeded UUID generation.
 * Not intended for concurrent workloads.
 */
public class SeededUUIDGenerator {

    private final Random random;

    protected SeededUUIDGenerator(long seed) {
        random = new Random(seed);
    }

    public UUID next() {
        return new UUID(random.nextLong(), random.nextLong());
    }

}
