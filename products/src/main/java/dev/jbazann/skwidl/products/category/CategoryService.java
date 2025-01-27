package dev.jbazann.skwidl.products.category;

import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    public Collection<Category> find(@NotNull Category probe) {
        return repository.findAll(Example.of(probe)); // TODO paging, example matcher
    }

    public Category newCategory(@NotNull CategoryDTO categoryDto) {
        Category category = categoryDto.toEntity();
        category.id(UUID.randomUUID());// TODO ids
        return repository.save(category);
    }
}
