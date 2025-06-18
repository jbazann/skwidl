package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.commons.exceptions.EntityNotFoundException;
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
        UUID categoryId = request.findCategoryByName(input.getCategoryName()).join();
        if (categoryId == null) throw new EntityNotFoundException(String.format(
                "No category found with name %s.", input.getCategoryName()
        ));
        dto.setCategory(categoryId);
        dto.setId(actions.generateProductId());
        dto.setCurrentStock(0);
        @Valid Product product = dto.toEntity();
        return actions.save(product);
    }

    public Product updateProduct(@NotNull @Valid ProvisioningDTO update) {
        Product product = actions.fetch(update.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No product found with id %s.", update.getProductId()
                )));
        boolean shouldUpdatePrice = update.getPrice() != null &&
                update.getPrice().compareTo(BigDecimal.ZERO) >= 0;
        boolean shouldUpdateStock = update.getUnits() != null &&
                update.getUnits() > 0;
        if (shouldUpdatePrice) product.setPrice(update.getPrice());
        if (shouldUpdateStock) product.setCurrentStock(product.getCurrentStock() + update.getUnits());
        return actions.save(product);
    }

    public Product discountProduct(@NotNull @Valid DiscountDTO discount) {
        Product product = actions.fetch(discount.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No product found with id %s.", discount.getProductId()
                )));
        product.setDiscount(discount.getDiscount());
        return actions.save(product);
    }

    // TODO this implementation is ass
    public AvailabilityResponse checkAvailability(@NotNull @NotEmpty List<@NotNull @Valid StockRequest> entries) {
        List<UUID> ids = entries.stream().map(StockRequest::getProductId).toList();
        Map<UUID, Product> productMap = actions.fetchAll(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        AvailabilityResponse response = new AvailabilityResponse();

        // Check that all products were found.
        final int amountNotFound = entries.size() - productMap.size();
        response.setProductsExist(amountNotFound == 0);

        response.setUnitCost(new HashMap<>());
        response.setTotalCost(BigDecimal.ZERO);
        List<UUID> notFoundIds = new ArrayList<>(amountNotFound);
        entries.forEach(e -> {
            Product p;
            if ((p = productMap.get(e.getProductId())) == null) {
                notFoundIds.add(e.getProductId());
            } else if (p.getCurrentStock() < e.getAmount()) {
                notFoundIds.add(e.getProductId());
            } else {
                BigDecimal discountedPrice = p.getPrice().subtract(p.getPrice().multiply(p.getDiscount()));
                response.getUnitCost().put(p.getId(), discountedPrice);
                response.setTotalCost(response.getTotalCost().add(
                        discountedPrice.multiply(BigDecimal.valueOf(e.getAmount()))
                ));
            }
        });

        if (!notFoundIds.isEmpty()) {
            response.setMissingProducts(new ArrayList<>());
            notFoundIds.forEach(id -> response.getMissingProducts().add(id));
        }

        return response;
    }

    public void reserveProducts(@NotNull @NotEmpty List<@NotNull @Valid StockRequest> entries) {
        Collection<Product> products = actions.fetchAll(entries.stream().map(StockRequest::getProductId).toList());
        Map<UUID, Product> productMap = new HashMap<>();
        products.forEach(p -> productMap.put(p.getId(),p));
        entries.forEach(e -> {
            Product p = productMap.get(e.getProductId());
            if (p.getCurrentStock() < e.getAmount()) throw new InsufficientStockException();
            p.setCurrentStock(p.getCurrentStock() - e.getAmount());
        });
        actions.saveAll(products);
    }
}