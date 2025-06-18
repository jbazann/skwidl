package dev.jbazann.skwidl.products.product.dto;

import jakarta.validation.constraints.Min;
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
public class DiscountDTO {

    @NotNull
    private UUID productId;
    @NotNull @Min(0)
    private BigDecimal discount;

}
