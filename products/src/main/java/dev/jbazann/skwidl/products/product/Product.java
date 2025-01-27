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
@Table(name = "product",schema = "products")
public class Product {

    @Column @Id
    @NotNull
    private UUID id;
    @Column
    @NotNull @NotEmpty
    private String name;
    @Column
    @NotNull
    private String description;
    @Column
    @NotNull @Min(0)
    private BigDecimal price;
    @Column
    @Min(0)
    private int currentStock;
    @Column
    @Min(0)
    private int minimumStock;
    @Column
    @NotNull
    private UUID category;

    public ProductDTO toDto() {
        return new ProductDTO(id, name, description, price, currentStock, minimumStock, category);
    }

}
