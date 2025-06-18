package dev.jbazann.skwidl.products.integration.testdata;

import dev.jbazann.skwidl.products.category.Category;
import dev.jbazann.skwidl.products.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
public class IntegrationDatasetEntry {

    private Product product;
    private List<Product> products;
    private Category category;
    private List<Category> categories;



}
