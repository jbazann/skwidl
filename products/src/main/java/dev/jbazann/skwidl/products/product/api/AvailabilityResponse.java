package dev.jbazann.skwidl.products.product.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class AvailabilityResponse {

    private Boolean productsExist;
    private List<UUID> missingProducts;
    private Map<UUID, BigDecimal> unitCost;
    private BigDecimal totalCost;

}
