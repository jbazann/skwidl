package dev.jbazann.skwidl.products.product.testdata;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.commons.utils.FunStuff;

import java.math.BigDecimal;
import java.util.Random;

public class ProductDatasetEntryStaticGenerator {

    private static final String MUSE =
            "The good old days were long gone; " +
            "And now her voice is but a ripple; " +
            "As I dress her pretty, to break the news; " +
            "Oh bittersweet beauty, my life's muse";
    private static final long SEED = MUSE.chars().sum();
    private static final Random RANDOM = new Random(SEED);
    private static final SeededUUIDGenerator UUID_GENERATOR = new SeededUUIDGenerator(RANDOM);
    private static final ProductGenerator PRODUCT_GENERATOR = new ProductGenerator(RANDOM, UUID_GENERATOR);

    public static ProductDatasetEntry genericEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct());
    }

    public static ProductDatasetEntry createProductEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.newlyCreatedProduct());
    }

    public static ProductDatasetEntry genericEntryList() {
        final int AMOUNT = 5;
        return new ProductDatasetEntry()
                .products(FunStuff.nElemList(PRODUCT_GENERATOR::genericProduct,AMOUNT));
    }

    public static ProductDatasetEntry nullNameEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .name(null));
    }

    public static ProductDatasetEntry emptyNameEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .name(""));
    }

    public static ProductDatasetEntry negativePriceEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .price(new BigDecimal("-0.01")));
    }

    public static ProductDatasetEntry zeroPriceEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .price(BigDecimal.ZERO));
    }

    public static ProductDatasetEntry nullPriceEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .price(null));
    }

    public static ProductDatasetEntry nullDescriptionEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .description(null));
    }

    public static ProductDatasetEntry emptyDescriptionEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                    .description(""));
    }

    public static ProductDatasetEntry negativeStockEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .currentStock(-1));
    }

    public static ProductDatasetEntry negativeMinStockEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .minimumStock(-1));
    }

    public static ProductDatasetEntry nullCategoryEntry() {
        return new ProductDatasetEntry()
                .product(PRODUCT_GENERATOR.genericProduct()
                        .category(null));
    }

}
