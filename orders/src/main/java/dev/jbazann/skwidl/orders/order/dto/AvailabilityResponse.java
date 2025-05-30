package dev.jbazann.skwidl.orders.order.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.jbazann.skwidl.orders.order.services.ProductServiceRestClient.*;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@ToString
public class AvailabilityResponse {

    private Boolean productsExist;
    private List<UUID> missingProducts;
    private Map<UUID, BigDecimal> unitCost;
    private BigDecimal totalCost;

    @SuppressWarnings("unchecked")
    public static AvailabilityResponse fromTheSillyMap(Map<String, Object> hehe) {
        Map<UUID, BigDecimal> unitCost = ((Map<String, String>) hehe.get(UNIT_COST))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        (entry) -> UUID.fromString(entry.getKey()),
                        (entry) -> new BigDecimal(entry.getValue())
                ));
        return new AvailabilityResponse()
                .productsExist((Boolean)hehe.get(PRODUCTS_EXIST))
                .missingProducts((List<UUID>)hehe.get("missingProducts"))
                .unitCost(unitCost)
                .totalCost(new BigDecimal((String)hehe.get(TOTAL_COST)));
    }

}
