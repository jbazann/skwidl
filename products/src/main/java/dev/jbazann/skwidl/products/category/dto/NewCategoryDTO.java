package dev.jbazann.skwidl.products.category.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class NewCategoryDTO {

    @NotBlank
    private String name;

    public CategoryDTO toDto() {
        return new CategoryDTO().name(name);
    }

}
