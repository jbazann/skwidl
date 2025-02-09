package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.products.category.Category;
import dev.jbazann.skwidl.products.category.CategoryService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class CategoryServiceLocalClient implements CategoryServiceClient {

    private final CategoryService categoryService;

    public CategoryServiceLocalClient(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CompletableFuture<UUID> findCategoryByName(String name) {
        Category probe = new Category().name(name); // keep id null
        return CompletableFuture.completedFuture(
                categoryService.find(probe)
                        .stream()
                        .findFirst()
                        .orElse(probe) // has null id
                        .id()
        );
    }

}
