package dev.jbazann.skwidl.products.product.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.products.product.Product;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class ProductDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private int currentStock;
    private int minimumStock;
    private UUID category;

    public Product toEntity() {
        return new Product(id, name, description, price, discount, currentStock, minimumStock, category);
    }

}
