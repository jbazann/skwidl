package com.jbazann.products.product.testdata;

import com.jbazann.products.product.Product;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

import static com.jbazann.commons.utils.FunStuff.nElemList;
import static com.jbazann.products.product.testdata.ProductDatasetEntryStaticGenerator.*;

@Getter
@Accessors(fluent = true)
public enum ProductDataset {

    PERSISTED(genericEntryList()),
    CREATE_PRODUCT(createProductEntry()),
    NOT_PERSISTED(genericEntry()),
    EMPTY_NAME(emptyNameEntry()),
    NULL_NAME(nullNameEntry()),
    EMPTY_DESCRIPTION(emptyDescriptionEntry()),
    NULL_DESCRIPTION(nullDescriptionEntry()),
    NEGATIVE_PRICE(negativePriceEntry()),
    NULL_PRICE(nullPriceEntry()),
    NEGATIVE_STOCK(negativeStockEntry()),
    NEGATIVE_MIN_STOCK(negativeMinStockEntry()),
    NULL_CATEGORY(nullCategoryEntry()),
    ;

    private final ProductDatasetEntry entry;

    ProductDataset(ProductDatasetEntry entry) {
        this.entry = entry;
    }

    public static final List<Product> PERSISTED_PRODUCTS = List.copyOf(buildPersistedProducts());

    public static List<Product> buildPersistedProducts() {
        final int AMOUNT = 50;
        final List<ProductDatasetEntry> persistedProducts =
                nElemList(ProductDatasetEntryStaticGenerator::genericEntry,AMOUNT);
        persistedProducts.add(PERSISTED.entry);
        return persistedProducts.stream().map(ProductDatasetEntry::product).toList();
    }

}
