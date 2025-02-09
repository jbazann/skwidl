package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.commons.exceptions.EntityNotFoundException;
import dev.jbazann.skwidl.commons.exceptions.UnknownErrorException;
import dev.jbazann.skwidl.products.product.api.AvailabilityResponse;
import dev.jbazann.skwidl.products.product.api.StockRequest;
import dev.jbazann.skwidl.products.product.dto.DiscountDTO;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProvisioningDTO;
import dev.jbazann.skwidl.products.product.exceptions.InsufficientStockException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;

@Service
@Validated
public class ProductService {

    private final ProductLifecycleActions actions;
    private final CategoryServiceClient request;

    public ProductService(ProductLifecycleActions actions, CategoryServiceClient request) {
        this.actions = actions;
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
        return actions.save(product);
    }

    public Product updateProduct(@NotNull @Valid ProvisioningDTO update) {
        Product product = actions.fetch(update.productId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No product found with id %s.", update.productId()
                )));
        boolean shouldUpdatePrice = update.price() != null &&
                update.price().compareTo(BigDecimal.ZERO) >= 0;
        boolean shouldUpdateStock = update.units() != null &&
                update.units() > 0;
        if (shouldUpdatePrice) product.price(update.price());
        if (shouldUpdateStock) product.currentStock(product.currentStock() + update.units());
        return actions.save(product);
    }

    public Product discountProduct(@NotNull @Valid DiscountDTO discount) {
        Product product = actions.fetch(discount.productId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No product found with id %s.", discount.productId()
                )));
        product.discount(discount.discount());
        return actions.save(product);
    }

    public AvailabilityResponse checkAvailability(@NotNull @NotEmpty List<@NotNull @Valid StockRequest> entries) {
        Collection<Product> products = actions.fetchAll(entries.stream().map(StockRequest::productId).toList());
        AvailabilityResponse response = new AvailabilityResponse();

        response.productsExist(products.size() == entries.size());

        response.unitCost(new HashMap<>());
        products.forEach(p -> {
            response.unitCost().put(p.id(), p.price().subtract(p.price().multiply(p.discount())));
            entries.removeIf(e -> e.productId().equals(p.id())); // TODO optimize
        });

        if (!entries.isEmpty()) {
            response.missingProducts(new ArrayList<>());
            entries.forEach(e -> {
                response.missingProducts().add(e.productId());
            });
        }

        response.totalCost(
                response.unitCost().values().stream().reduce(BigDecimal::add).
                        orElseThrow(UnknownErrorException::new)
        );

        return response;
    }

    public void reserveProducts(@NotNull @NotEmpty List<@NotNull @Valid StockRequest> entries) {
        Collection<Product> products = actions.fetchAll(entries.stream().map(StockRequest::productId).toList());
        Map<UUID, Product> productMap = new HashMap<>();
        products.forEach(p -> productMap.put(p.id(),p));
        entries.forEach(e -> {
            Product p = productMap.get(e.productId());
            if (p.currentStock() < e.amount()) throw new InsufficientStockException();
            p.currentStock(p.currentStock() - e.amount());
        });
        actions.saveAll(products);
    }
}