package com.jbazann.products.category.testdata;

import com.jbazann.products.category.Category;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

import static com.jbazann.products.category.testdata.CagetoryDatasetEntryStaticGenerator.*;
import static com.jbazann.commons.utils.FunStuff.nElemList;

@Getter
@Accessors(fluent = true)
public enum CategoryDataset {

    PERSISTED(genericEntryList()),
    NOT_PERSISTED(genericEntry()),
    EMPTY_NAME(emptyNameEntry()),
    NULL_NAME(nullNameEntry())
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
                nElemList(CagetoryDatasetEntryStaticGenerator::genericEntry,AMOUNT);
        return persistedCategories.stream().map(CategoryDatasetEntry::category).toList();
    }

}
