package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.commons.exceptions.EntityNotFoundException;
import dev.jbazann.skwidl.products.product.dto.DiscountDTO;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProvisioningDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductRequestService request;

    public ProductService(ProductRepository repository, ProductRequestService request) {
        this.repository = repository;
        this.request = request;
    }

    public Product newProduct(@NotNull @Valid NewProductDTO dto) {
        Product product = dto.toEntity();
        UUID categoryId = request.findCategoryByName(dto.categoryName()).join();
        if (categoryId == null) throw new EntityNotFoundException(String.format(
                "No category found with name %s.", dto.categoryName()
        ));
        product.category(categoryId);
        product.id(UUID.randomUUID());//TODO ids
        product.currentStock(0);
        return repository.save(product);
    }

    public Product updateProduct(@NotNull @Valid ProvisioningDTO update) {
        Product product = findById(update.productId());
        final boolean shouldUpdatePrice = update.price() != null &&
                update.price().compareTo(BigDecimal.ZERO) >= 0;
        final boolean shouldUpdateStock = update.units() != null &&
                update.units() > 0;
        if (shouldUpdatePrice) product.price(update.price());
        if (shouldUpdateStock) product.currentStock(product.currentStock() + update.units());
        return repository.save(product);
    }

    public Product discountProduct(@NotNull @Valid DiscountDTO discount) {
        Product product = findById(discount.productId());
        return repository.save(product.discount(discount.discount()));
    }

    private Product findById(@NotNull UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No product found with id %s.", id
                )));
    }

}