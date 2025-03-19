package dev.jbazann.skwidl.products.product.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class StockRequest {

    @NotNull
    private UUID productId;
    @NotNull @Min(1)
    private Integer amount;

}
