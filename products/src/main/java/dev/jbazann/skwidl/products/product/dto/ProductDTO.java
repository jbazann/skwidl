package dev.jbazann.skwidl.products.product.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public class ProductDTO {

    private UUID id;
    private String name;

}
