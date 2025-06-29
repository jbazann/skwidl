package dev.jbazann.skwidl.products.product;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "product",schema = "product")
public class Product {

    @Id
    @NotNull
    private UUID id;
    @NotBlank @Length(max = 511)
    private String name;
    @NotNull @Length(max = 2047)
    private String description;
    @NotNull @Min(0)
    private BigDecimal price;
    @NotNull @Min(0)
    private BigDecimal discount;
    @Min(0)
    @Column(name = "current_stock")
    private int currentStock;
    @Min(0)
    @Column(name = "minimum_stock")
    private int minimumStock;
    @NotNull
    private UUID category;

    public ProductDTO toDto() {
        return new ProductDTO(id, name, description, price, discount, currentStock, minimumStock, category);
    }

}
