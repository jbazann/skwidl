package dev.jbazann.skwidl.products.category;

import dev.jbazann.skwidl.commons.exceptions.BadRequestException;
import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import dev.jbazann.skwidl.products.category.dto.NewCategoryDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category")
    public List<CategoryDTO> findCategory(@RequestParam Map<String, String> params) {
        if (!params.containsKey("name") && !params.containsKey("id"))
            throw new BadRequestException("Query parameter 'name' and/or 'id' required.");
        return categoryService.find(new Category()
                .id(params.containsKey("id") ? UUID.fromString(params.get("id")) : null)
                .name(params.get("name"))
        ).stream().map(Category::toDto).toList();
    }

    @PostMapping("/category")
    public CategoryDTO createCategory(@RequestBody NewCategoryDTO category) {
        return categoryService.newCategory(category).toDto();
    }
}
