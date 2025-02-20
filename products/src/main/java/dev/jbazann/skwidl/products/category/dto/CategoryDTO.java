package dev.jbazann.skwidl.products.category.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.products.category.Category;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class CategoryDTO {

    private UUID id;
    private String name;

    public Category toEntity() {
        return new Category(id, name);
    }
}
