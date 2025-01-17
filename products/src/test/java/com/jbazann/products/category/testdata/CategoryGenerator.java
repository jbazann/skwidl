package com.jbazann.products.category.testdata;

import com.jbazann.commons.testing.SeededUUIDGenerator;
import com.jbazann.products.category.Category;

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
                .id(UUID_GENERATOR.next())
                .name(nextName());
    }

    private String nextName() {
        final int RANDOM_NAME_DIGIT_COUNT = 6;
        return String.format("CATEGORY_%d",
                RANDOM.nextInt((int)Math.pow(10, RANDOM_NAME_DIGIT_COUNT)));
    }

}
