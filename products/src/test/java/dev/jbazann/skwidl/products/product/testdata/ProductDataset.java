package dev.jbazann.skwidl.products.product.testdata;

import dev.jbazann.skwidl.products.product.Product;
import dev.jbazann.skwidl.commons.utils.FunStuff;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(fluent = true)
public enum ProductDataset {

    PERSISTED(ProductDatasetEntryStaticGenerator.genericEntryList()),
    CREATE_PRODUCT(ProductDatasetEntryStaticGenerator.createProductEntry()),
    NOT_PERSISTED(ProductDatasetEntryStaticGenerator.genericEntry()),
    EMPTY_NAME(ProductDatasetEntryStaticGenerator.emptyNameEntry()),
    NULL_NAME(ProductDatasetEntryStaticGenerator.nullNameEntry()),
    EMPTY_DESCRIPTION(ProductDatasetEntryStaticGenerator.emptyDescriptionEntry()),
    NULL_DESCRIPTION(ProductDatasetEntryStaticGenerator.nullDescriptionEntry()),
    NEGATIVE_PRICE(ProductDatasetEntryStaticGenerator.negativePriceEntry()),
    NULL_PRICE(ProductDatasetEntryStaticGenerator.nullPriceEntry()),
    NEGATIVE_STOCK(ProductDatasetEntryStaticGenerator.negativeStockEntry()),
    NEGATIVE_MIN_STOCK(ProductDatasetEntryStaticGenerator.negativeMinStockEntry()),
    NULL_CATEGORY(ProductDatasetEntryStaticGenerator.nullCategoryEntry()),
    ;

    private final ProductDatasetEntry entry;

    ProductDataset(ProductDatasetEntry entry) {
        this.entry = entry;
    }

    public static final List<Product> PERSISTED_PRODUCTS = List.copyOf(buildPersistedProducts());

    public static List<Product> buildPersistedProducts() {
        final int AMOUNT = 50;
        final List<ProductDatasetEntry> persistedProducts =
                FunStuff.nElemList(ProductDatasetEntryStaticGenerator::genericEntry,AMOUNT);
        persistedProducts.add(PERSISTED.entry);
        return persistedProducts.stream().map(ProductDatasetEntry::product).toList();
    }

}
