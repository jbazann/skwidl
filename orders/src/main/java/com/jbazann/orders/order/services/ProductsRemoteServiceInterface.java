package com.jbazann.orders.order.services;

import com.jbazann.orders.order.dto.ProductAmountDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProductsRemoteServiceInterface {

    CompletableFuture<Map<String, Object>> validateProductAndFetchCost(List<Map<String, Object>> batch);
    Boolean reserveProducts(Map< UUID, ProductAmountDTO > products);

}
