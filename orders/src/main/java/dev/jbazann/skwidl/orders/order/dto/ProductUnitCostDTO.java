package dev.jbazann.skwidl.orders.order.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dev.jbazann.skwidl.commons.utils.Patterns.UUIDPattern;

public record ProductUnitCostDTO(UUID id, BigDecimal unitCost) {

    public static Map<UUID,ProductUnitCostDTO> fromValidatedBatch(Map<String, Object> map) {
        HashMap<UUID,ProductUnitCostDTO> fixed = new HashMap<>();
        map.entrySet().stream()
                // filter entries in format <String productId, BigDecimal unitCost>
                .filter(entry -> UUIDPattern.test(entry.getKey()) && entry.getValue() instanceof BigDecimal)
                .forEach(entry -> fixed.put(
                        UUID.fromString(entry.getKey()),
                        new ProductUnitCostDTO(UUID.fromString(entry.getKey()),(BigDecimal) entry.getValue()))
                );
        return fixed;
    }
}
