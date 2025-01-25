package dev.jbazann.skwidl.products.product;

import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import jakarta.persistence.*;
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

    @Column @Id private UUID id;
    @Column private String name;
    @Column private String description;
    @Column private BigDecimal price;
    @Column private int currentStock;
    @Column private int minimumStock;
    @Column private UUID category;

    public ProductDTO toDto() {
        return null;
    }
}
