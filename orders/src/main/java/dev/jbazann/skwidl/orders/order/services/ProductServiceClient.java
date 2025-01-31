package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.dto.ProductAmountDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProductServiceClient {

    CompletableFuture<Map<String, Object>> validateProductAndFetchCost(@NotNull List<Map<String, Object>> batch);
    Boolean reserveProducts(@NotNull Map<UUID, ProductAmountDTO> products);

}
