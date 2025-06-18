package dev.jbazann.skwidl.products.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class NewCategoryDTO {

    @NotBlank
    private String name;

    public CategoryDTO toDto() {
        return new CategoryDTO().setName(name);
    }

}
