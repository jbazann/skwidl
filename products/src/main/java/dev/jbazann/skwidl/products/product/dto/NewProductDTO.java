package dev.jbazann.skwidl.products.product.dto;

import dev.jbazann.skwidl.products.product.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public class NewProductDTO {

    @NotNull @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull @NotEmpty
    private String categoryName;
    @Min(0)
    private int minStock;
    @NotNull @Min(0)
    private BigDecimal price;
    @NotNull @Min(0)
    private BigDecimal discount;// TODO

    public Product toEntity() {
        return new Product()
                .name(name)
                .description(description)
                .minimumStock(minStock)
                .price(price)
                .discount(discount);
    }
}
