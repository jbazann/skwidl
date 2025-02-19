package dev.jbazann.skwidl.products.category.dto;

import dev.jbazann.skwidl.products.category.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public class NewCategoryDTO {

    @NotNull @NotEmpty
    private String name;

    public Category toEntity() {
        return new Category().name(name);
    }

}
