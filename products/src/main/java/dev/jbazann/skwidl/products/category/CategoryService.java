package dev.jbazann.skwidl.products.category;

import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import dev.jbazann.skwidl.products.category.dto.NewCategoryDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.UUID;

@Service
@Validated
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    public UUID generateCategoryId() {
        UUID id;
        while (repository.existsById(id = UUID.randomUUID()));
        return id;
    }

    public Collection<Category> find(@NotNull Category probe) {
        return repository.findAll(Example.of(probe)); // TODO paging, example matcher
    }

    public Category newCategory(@NotNull @Valid NewCategoryDTO input) {
        CategoryDTO dto = input.toDto();
        dto.id(generateCategoryId());
        @Valid Category category = dto.toEntity();
        return repository.save(category);
    }
}
