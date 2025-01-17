package com.jbazann.products.category.testdata;

import com.jbazann.products.category.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class CategoryDatasetEntry {

    private Category category;
    private List<Category> categories;

    public CategoryDTO asCategoryDTO() {
        return category.toDto();
    }

    public List<CategoryDTO> asCategoryDTOList() {
        return categories.stream().map(Category::toDto).toList();
    }

    public NewCategoryDTO asNewCategoryDTO() {
        return new NewCategoryDTO(category.name());
    }

}
