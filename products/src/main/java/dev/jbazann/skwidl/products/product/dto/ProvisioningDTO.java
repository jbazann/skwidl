package dev.jbazann.skwidl.products.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class ProvisioningDTO {

    @NotNull
    private UUID productId;
    //TODO one of these two must be present
    private Integer units;
    private BigDecimal price;

}
