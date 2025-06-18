package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.products.product.api.AvailabilityResponse;
import dev.jbazann.skwidl.products.product.api.StockRequest;
import dev.jbazann.skwidl.products.product.dto.DiscountDTO;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProvisioningDTO;
import dev.jbazann.skwidl.products.product.exceptions.InsufficientStockException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(params = "!operation")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody NewProductDTO product) {
        return productService.newProduct(product).toDto();
    }

    @PostMapping(params = "operation=availability")
    public AvailabilityResponse checkAvailability(@RequestBody List<StockRequest> entries) {
        return productService.checkAvailability(entries);
    }

    @PostMapping(params = "operation=reserve")
    public void reserveProducts(@RequestBody List<StockRequest> entries) {
        productService.reserveProducts(entries);
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable UUID id, @RequestBody ProvisioningDTO update) {
        return productService.updateProduct(update.setProductId(id)).toDto();
    }

    @PutMapping("/{id}/discount")
    public ProductDTO discountProduct(@PathVariable UUID id, @RequestBody DiscountDTO discount) {
        return productService.discountProduct(discount.setProductId(id)).toDto();
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInsufficientStockException(InsufficientStockException exception) {
        return exception.getMessage();
    }
}
