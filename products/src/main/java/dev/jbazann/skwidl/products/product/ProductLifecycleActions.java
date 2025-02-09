package dev.jbazann.skwidl.products.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class ProductLifecycleActions {

    private final ProductRepository productRepository;

    public ProductLifecycleActions(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> fetch(@NotNull UUID id) {
        return productRepository.findById(id);
    }

    public Collection<Product> fetchAll(@NotNull @NotEmpty Iterable<@NotNull UUID> ids) {
        return productRepository.findAllById(ids);
    }

    public Product save(@NotNull @Valid Product product) {
        return productRepository.save(product);
    }

    public Collection<Product> saveAll(@NotNull @NotEmpty Collection<@Valid Product> products) {
        return productRepository.saveAll(products);
    }
}
