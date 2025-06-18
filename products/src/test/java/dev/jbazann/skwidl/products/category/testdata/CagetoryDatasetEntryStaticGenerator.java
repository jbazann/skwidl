package dev.jbazann.skwidl.products.category.testdata;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.commons.utils.FunStuff;

import java.util.Random;

public class CagetoryDatasetEntryStaticGenerator {

    private static final String MUSE =
            "The good old days were long gone; " +
            "And now her voice is but a ripple; " +
            "As I dress her pretty, to break the news; " +
            "Oh bittersweet beauty, my life's muse";
    private static final long SEED = MUSE.chars().sum();
    private static final Random RANDOM = new Random(SEED);
    private static final SeededUUIDGenerator UUID_GENERATOR = new SeededUUIDGenerator(RANDOM);
    private static final CategoryGenerator CATEGORY_GENERATOR = new CategoryGenerator(RANDOM, UUID_GENERATOR);

    public static CategoryDatasetEntry genericEntry() {
        return new CategoryDatasetEntry()
                .setCategory(CATEGORY_GENERATOR.genericCategory());
    }

    public static CategoryDatasetEntry genericEntryList() {
        final int AMOUNT = 5;
        return new CategoryDatasetEntry()
                .setCategories(FunStuff.nElemList(CATEGORY_GENERATOR::genericCategory,AMOUNT));
    }

    public static CategoryDatasetEntry nullNameEntry() {
        return new CategoryDatasetEntry()
                .setCategory(CATEGORY_GENERATOR.genericCategory()
                        .setName(null));
    }

    public static CategoryDatasetEntry emptyNameEntry() {
        return new CategoryDatasetEntry()
                .setCategory(CATEGORY_GENERATOR.genericCategory()
                        .setName(""));
    }

}
