package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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



}
