package dev.jbazann.skwidl.products.category;

import dev.jbazann.skwidl.commons.exceptions.BadRequestException;
import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import dev.jbazann.skwidl.products.category.dto.NewCategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDTO> findCategory(@RequestParam Map<String, String> params) {
        if (!params.containsKey("name") && !params.containsKey("id"))
            throw new BadRequestException("Query parameter 'name' and/or 'id' required.");
        return categoryService.find(new Category()
                .setId(params.containsKey("id") ? UUID.fromString(params.get("id")) : null)
                .setName(params.get("name"))
        ).stream().map(Category::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@RequestBody NewCategoryDTO category) {
        return categoryService.newCategory(category).toDto();
    }
}
