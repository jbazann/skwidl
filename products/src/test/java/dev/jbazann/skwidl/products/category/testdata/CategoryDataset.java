package dev.jbazann.skwidl.products.category.testdata;

import dev.jbazann.skwidl.products.category.Category;
import dev.jbazann.skwidl.commons.utils.FunStuff;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(fluent = true)
public enum CategoryDataset {

    PERSISTED(CagetoryDatasetEntryStaticGenerator.genericEntryList()),
    NOT_PERSISTED(CagetoryDatasetEntryStaticGenerator.genericEntry()),
    EMPTY_NAME(CagetoryDatasetEntryStaticGenerator.emptyNameEntry()),
    NULL_NAME(CagetoryDatasetEntryStaticGenerator.nullNameEntry())
    // TODO other name constraints?
    ;

    private final CategoryDatasetEntry entry;

    CategoryDataset(CategoryDatasetEntry entry) {
        this.entry = entry;
    }

    public static final List<Category> PERSISTED_CATEGORIES = List.copyOf(buildPersistedCategories());

    public static List<Category> buildPersistedCategories() {
        final int AMOUNT = 50;
        final List<CategoryDatasetEntry> persistedCategories =
                FunStuff.nElemList(CagetoryDatasetEntryStaticGenerator::genericEntry,AMOUNT);
        return persistedCategories.stream().map(CategoryDatasetEntry::category).toList();
    }

}
