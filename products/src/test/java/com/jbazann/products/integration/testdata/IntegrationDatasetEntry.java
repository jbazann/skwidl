package com.jbazann.products.integration.testdata;

import com.jbazann.products.category.Category;
import com.jbazann.products.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class IntegrationDatasetEntry {

    private Product product;
    private List<Product> products;
    private Category category;
    private List<Category> categories;



}
