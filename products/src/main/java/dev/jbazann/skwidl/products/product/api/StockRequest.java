package dev.jbazann.skwidl.products.product.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public class StockRequest {

    @NotNull
    private UUID productId;
    @NotNull @Min(1)
    private Integer amount;

}
