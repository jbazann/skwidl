package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.products.product.dto.DiscountDTO;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProvisioningDTO;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/product")
    public ProductDTO createProduct(@RequestBody NewProductDTO product) {
        return productService.newProduct(product).toDto();
    }

    @PutMapping("/product/{id}")
    public ProductDTO updateProduct(@PathVariable UUID id, @RequestBody ProvisioningDTO update) {
        return productService.updateProduct(update.productId(id)).toDto();
    }

    @PutMapping("/product/{id}")
    public ProductDTO discountProduct(@PathVariable UUID id, @RequestBody DiscountDTO discount) {
        return productService.discountProduct(discount.productId(id)).toDto();
    }

}
