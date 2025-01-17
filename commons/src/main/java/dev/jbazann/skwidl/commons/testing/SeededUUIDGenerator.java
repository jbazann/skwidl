package dev.jbazann.skwidl.commons.testing;

import java.util.Random;
import java.util.UUID;

/**
 * Basic utility class to simplify seeded UUID generation.
 * Not intended for concurrent workloads.
 */
public class SeededUUIDGenerator {

    private final Random random;

    public SeededUUIDGenerator(long seed) {
        random = new Random(seed);
    }

    public SeededUUIDGenerator(Random random) {
        this.random = random;
    }

    public UUID next() {
        return new UUID(random.nextLong(), random.nextLong());
    }

}
