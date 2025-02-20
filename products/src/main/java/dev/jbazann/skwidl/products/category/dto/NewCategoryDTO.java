package dev.jbazann.skwidl.products.category.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.products.category.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull @NotEmpty
    private String name;

    public Category toEntity() {
        return new Category().name(name);
    }

}
