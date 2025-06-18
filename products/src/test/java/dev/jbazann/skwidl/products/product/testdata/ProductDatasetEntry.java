package dev.jbazann.skwidl.products.product.testdata;

import dev.jbazann.skwidl.products.product.Product;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
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
        return new NewProductDTO()
                .setName(product.getName());
    }

}
