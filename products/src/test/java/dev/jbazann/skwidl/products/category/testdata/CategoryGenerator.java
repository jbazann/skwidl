package dev.jbazann.skwidl.products.category.testdata;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.products.category.Category;

import java.util.Random;

public class CategoryGenerator {

    private final Random RANDOM;
    private final SeededUUIDGenerator UUID_GENERATOR;

    public CategoryGenerator(Random RANDOM, SeededUUIDGenerator UUID_GENERATOR) {
        this.RANDOM = RANDOM;
        this.UUID_GENERATOR = UUID_GENERATOR;
    }

    public Category genericCategory() {
        return new Category()
                .setId(UUID_GENERATOR.next())
                .setName(nextName());
    }

    private String nextName() {
        final int RANDOM_NAME_DIGIT_COUNT = 6;
        return String.format("CATEGORY_%d",
                RANDOM.nextInt((int)Math.pow(10, RANDOM_NAME_DIGIT_COUNT)));
    }

}
