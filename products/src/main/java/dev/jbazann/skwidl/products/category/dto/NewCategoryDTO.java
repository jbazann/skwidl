package dev.jbazann.skwidl.products.category.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public class NewCategoryDTO {

    private String name;

}
