package dev.jbazann.skwidl.products.product;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductLifecycleActions {

    private final ProductRepository productRepository;

    public ProductLifecycleActions(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> fetch(UUID id) {
        return productRepository.findById(id);
    }

    public Collection<Product> fetchAll(Iterable<UUID> ids) {
        return productRepository.findAllById(ids);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Collection<Product> saveAll(Collection<Product> products) {
        return productRepository.saveAll(products);
    }
}
