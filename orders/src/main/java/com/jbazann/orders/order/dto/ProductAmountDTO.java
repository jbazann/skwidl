package com.jbazann.orders.order.dto;

import com.jbazann.orders.order.entities.Detail;
import com.jbazann.orders.order.entities.Order;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProductAmountDTO(@NotNull UUID id, @NotNull Integer amount) {
    @Validated
    public static Map<UUID,ProductAmountDTO> fromOrder(@NotNull Order order) {
        return order.detail().stream()
                .collect(Collectors.toMap(
                        Detail::product,
                        d -> new ProductAmountDTO(d.product(),d.amount())
                ));
    }
}
