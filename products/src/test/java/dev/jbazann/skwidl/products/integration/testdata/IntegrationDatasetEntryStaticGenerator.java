package dev.jbazann.skwidl.products.integration.testdata;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.products.category.Category;
import dev.jbazann.skwidl.products.category.testdata.CategoryGenerator;
import dev.jbazann.skwidl.products.product.Product;
import dev.jbazann.skwidl.products.product.testdata.ProductGenerator;

import java.util.List;
import java.util.Random;

import static dev.jbazann.skwidl.commons.utils.FunStuff.nElemList;

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
        p.setCategory(c.getId());
        return new IntegrationDatasetEntry()
                .setProduct(p)
                .setCategory(c);
    }

    public static IntegrationDatasetEntry productsInCategoryEntry() {
        final int PRODUCTS_IN_CATEGORY = 6;
        List<Product> p = nElemList(PRODUCT_GENERATOR::genericProduct, PRODUCTS_IN_CATEGORY);
        Category c = CATEGORY_GENERATOR.genericCategory();
        p.forEach(prod -> prod.setCategory(c.getId()));
        return new IntegrationDatasetEntry()
                .setProducts(p)
                .setCategory(c);
    }

}
