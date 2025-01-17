package com.jbazann.products.integration.testdata;

import com.jbazann.commons.testing.SeededUUIDGenerator;
import com.jbazann.products.category.Category;
import com.jbazann.products.category.testdata.CategoryGenerator;
import com.jbazann.products.product.Product;
import com.jbazann.products.product.testdata.ProductGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static com.jbazann.commons.utils.FunStuff.nElemList;

public class IntegrationDatasetEntryStaticGenerator {

    private static final String MUSE =
            "The good old days were long gone; " +
            "And now her voice is but a ripple; " +
            "As I dress her pretty, to break the news; " +
            "Oh bittersweet beauty, my life's muse";
    private static final long SEED = MUSE.chars().sum();
    private static final Random RANDOM = new Random(SEED);
    private static final SeededUUIDGenerator UUID_GENERATOR = new SeededUUIDGenerator(RANDOM);
    private static final ProductGenerator PRODUCT_GENERATOR = new ProductGenerator(RANDOM, UUID_GENERATOR);
    private static final CategoryGenerator CATEGORY_GENERATOR = new CategoryGenerator(RANDOM, UUID_GENERATOR);

    public static IntegrationDatasetEntry genericEntry() {
        Product p = PRODUCT_GENERATOR.genericProduct();
        Category c = CATEGORY_GENERATOR.genericCategory();
        p.category(c.id());
        return new IntegrationDatasetEntry()
                .product(p)
                .category(c);
    }

    public static IntegrationDatasetEntry productsInCategoryEntry() {
        final int PRODUCTS_IN_CATEGORY = 6;
        List<Product> p = nElemList(PRODUCT_GENERATOR::genericProduct, PRODUCTS_IN_CATEGORY);
        Category c = CATEGORY_GENERATOR.genericCategory();
        p.forEach(prod -> prod.category(c.id()));
        return new IntegrationDatasetEntry()
                .products(p)
                .category(c);
    }

}
