package com.jbazann.products.integration.testdata;

import com.jbazann.products.category.Category;
import com.jbazann.products.product.Product;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.jbazann.commons.utils.FunStuff.nElemList;
import static com.jbazann.products.integration.testdata.IntegrationDatasetEntryStaticGenerator.genericEntry;
import static com.jbazann.products.integration.testdata.IntegrationDatasetEntryStaticGenerator.productsInCategoryEntry;

@Getter
@Accessors(fluent = true)
public enum IntegrationDataset {

    PERSISTED_GENERIC(genericEntry()),
    PERSISTED_PRODUCTS_IN_CATEGORY(productsInCategoryEntry()),
    PERSISTED_MISSING_CATEGORY(genericEntry().category(null)),
    PERSISTED_EMPTY_CATEGORY(genericEntry().product(null)),
    GENERIC(genericEntry()),
    MISSING_CATEGORY(genericEntry().category(null)),
    EMPTY_CATEGORY(genericEntry().product(null)),

    ;

    private final IntegrationDatasetEntry entry;

    IntegrationDataset(IntegrationDatasetEntry entry) {
        this.entry = entry;
    }

    private static final List<IntegrationDatasetEntry> PERSISTED_ENTRIES = buildPersistedEntries();
    public static final List<Product> PERSISTED_PRODUCTS = List.copyOf(buildPersistedProducts());
    public static final List<Category> PERSISTED_CATEGORIES = List.copyOf(buildPersistedCategories());

    private static List<IntegrationDatasetEntry> buildPersistedEntries() {
        final int AMOUNT_SINGLE = 20;
        final int AMOUNT_LIST = 12;
        final List<IntegrationDatasetEntry> entries =
                nElemList(IntegrationDatasetEntryStaticGenerator::genericEntry, AMOUNT_SINGLE);
        entries.addAll(nElemList(IntegrationDatasetEntryStaticGenerator::productsInCategoryEntry, AMOUNT_LIST));
        entries.add(PERSISTED_GENERIC.entry);
        entries.add(PERSISTED_PRODUCTS_IN_CATEGORY.entry);
        entries.add(PERSISTED_MISSING_CATEGORY.entry);
        entries.add(PERSISTED_EMPTY_CATEGORY.entry);
        return entries;
    }

    private static List<Product> buildPersistedProducts() {
        return List.copyOf(PERSISTED_ENTRIES.stream().flatMap(e -> {
            if (e.product() != null) return Stream.of(e.product());
            if (e.products() != null) return e.products().stream();
            return null;
        }).filter(Objects::nonNull).toList());
    }

    private static List<Category> buildPersistedCategories() {
        return List.copyOf(PERSISTED_ENTRIES.stream().flatMap(e -> {
            if (e.category() != null) return Stream.of(e.category());
            if (e.categories() != null) return e.categories().stream();
            return null;
        }).filter(Objects::nonNull).toList());
    }

}
