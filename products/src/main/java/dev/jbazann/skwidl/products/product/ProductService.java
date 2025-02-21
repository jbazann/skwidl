package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.commons.exceptions.EntityNotFoundException;
import dev.jbazann.skwidl.commons.exceptions.UnknownErrorException;
import dev.jbazann.skwidl.products.product.api.AvailabilityResponse;
import dev.jbazann.skwidl.products.product.api.StockRequest;
import dev.jbazann.skwidl.products.product.dto.DiscountDTO;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProvisioningDTO;
import dev.jbazann.skwidl.products.product.exceptions.InsufficientStockException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
public class ProductService {

    private final ProductLifecycleActions actions;
    private final CategoryServiceClient request;

    public ProductService(ProductLifecycleActions actions, CategoryServiceClient request) {
        this.actions = actions;
        this.request = request;
    }

    public Product newProduct(@NotNull @Valid NewProductDTO input) {
        ProductDTO dto = input.toDto();
        UUID categoryId = request.findCategoryByName(input.categoryName()).join();
        if (categoryId == null) throw new EntityNotFoundException(String.format(
                "No category found with name %s.", input.categoryName()
        ));
        dto.category(categoryId);
        dto.id(actions.generateProductId());
        dto.currentStock(0);
        @Valid Product product = dto.toEntity();
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
        List<UUID> ids = entries.stream().map(StockRequest::productId).toList();
        Map<UUID, Product> productMap = actions.fetchAll(ids).stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));
        AvailabilityResponse response = new AvailabilityResponse();

        final int amountNotFound = entries.size() - productMap.size();
        response.productsExist(amountNotFound == 0);

        // Calculate unit costs
        response.unitCost(new HashMap<>());
        List<UUID> notFoundIds = new ArrayList<>(amountNotFound);
        entries.forEach(e -> {
            Product p;
            if ((p = productMap.get(e.productId())) == null) {
                notFoundIds.add(e.productId());
            } else {
                BigDecimal discountedPrice = p.price().subtract(p.price().multiply(p.discount()));
                response.unitCost().put(p.id(), discountedPrice);
            }
        });

        if (!notFoundIds.isEmpty()) {
            response.missingProducts(new ArrayList<>());
            notFoundIds.forEach(id -> response.missingProducts().add(id));
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