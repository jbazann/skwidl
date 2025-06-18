package dev.jbazann.skwidl.products.integration.testdata;

import dev.jbazann.skwidl.products.category.Category;
import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import dev.jbazann.skwidl.products.product.Product;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static dev.jbazann.skwidl.commons.utils.FunStuff.nElemList;

@Getter
@Accessors(fluent = true)
public enum IntegrationDataset {

    PERSISTED_GENERIC(IntegrationDatasetEntryStaticGenerator.genericEntry()),
    PERSISTED_PRODUCTS_IN_CATEGORY(IntegrationDatasetEntryStaticGenerator.productsInCategoryEntry()),
    PERSISTED_MISSING_CATEGORY(IntegrationDatasetEntryStaticGenerator.genericEntry().category(null)),
    PERSISTED_EMPTY_CATEGORY(IntegrationDatasetEntryStaticGenerator.genericEntry().product(null)),
    GENERIC(IntegrationDatasetEntryStaticGenerator.genericEntry()),
    MISSING_CATEGORY(IntegrationDatasetEntryStaticGenerator.genericEntry().category(null)),
    EMPTY_CATEGORY(IntegrationDatasetEntryStaticGenerator.genericEntry().product(null)),

    ;

    private final IntegrationDatasetEntry entry;

    IntegrationDataset(IntegrationDatasetEntry entry) {
        this.entry = entry;
    }

    private static final List<IntegrationDatasetEntry> PERSISTED_ENTRIES = buildPersistedEntries();
    public static final List<Product> PERSISTED_PRODUCTS = List.copyOf(buildPersistedProducts());
    public static final List<Category> PERSISTED_CATEGORIES = List.copyOf(buildPersistedCategories());

    public static void resetData() {
        //TODO
    }

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

    public CategoryDTO asCategoryDTO() {
        return this.entry.category().toDto();
    }

}
