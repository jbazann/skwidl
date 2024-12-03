package com.jbazann.orders.order.services;

import com.jbazann.orders.order.dto.ProductAmountDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProductsRemoteServiceInterface {

    CompletableFuture<Map<String, Object>> validateProductAndFetchCost(@NotNull List<Map<String, Object>> batch);
    Boolean reserveProducts(@NotNull Map<UUID, ProductAmountDTO> products);

}
