package com.jbazann.products.product.testdata;

import com.jbazann.products.product.Product;
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
@Accessors(chain = true, fluent = true)
public class ProductDatasetEntry {

    private Product product;
    private List<Product> products;

    public ProductDTO asProductDTO() {
        return product.toDto();
    }

    public List<ProductDTO> asProductDTOList() {
        return products.stream().map(Product::toDto).toList();
    }

    public NewProductDTO asNewProductDTO() {
        return new NewCategoryDTO(product.name());
    }

}
