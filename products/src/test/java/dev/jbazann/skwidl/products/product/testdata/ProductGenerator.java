package dev.jbazann.skwidl.products.product.testdata;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.products.product.Product;

import java.math.BigDecimal;
import java.util.Random;

public class ProductGenerator {

    private final Random RANDOM;
    private final SeededUUIDGenerator UUID_GENERATOR;

    public ProductGenerator(Random RANDOM, SeededUUIDGenerator UUID_GENERATOR) {
        this.RANDOM = RANDOM;
        this.UUID_GENERATOR = UUID_GENERATOR;
    }

    public Product genericProduct() {
        final int GENERIC_CURRENT_STOCK = 50; // TODO random
        final int GENERIC_MIN_STOCK = 20;
        final BigDecimal GENERIC_PRICE = new BigDecimal("25.5");
        return new Product()
                .id(UUID_GENERATOR.next())
                .name(nextName())
                .category(UUID_GENERATOR.next())
                .currentStock(GENERIC_CURRENT_STOCK)
                .minimumStock(GENERIC_MIN_STOCK)
                .price(GENERIC_PRICE);
    }

    public Product newlyCreatedProduct() {
        Product p = this.genericProduct();
        p.currentStock(0);
        return p;
    }

    private String nextName() {
        final int RANDOM_NAME_DIGIT_COUNT = 6;
        return String.format("PRODUCT_%d",
                RANDOM.nextInt((int)Math.pow(10, RANDOM_NAME_DIGIT_COUNT)));
    }

}
