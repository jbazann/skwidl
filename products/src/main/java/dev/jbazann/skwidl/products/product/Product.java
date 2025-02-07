package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "product",schema = "product")
public class Product {

    @Id
    @NotNull
    private UUID id;
    @NotNull @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull @Min(0)
    private BigDecimal price;
    @NotNull @Min(0)
    private BigDecimal discount;
    @Min(0)
    private int currentStock;
    @Min(0)
    private int minimumStock;
    @NotNull
    private UUID category;

    public ProductDTO toDto() {
        return new ProductDTO(id, name, description, price, discount, currentStock, minimumStock, category);
    }

}
