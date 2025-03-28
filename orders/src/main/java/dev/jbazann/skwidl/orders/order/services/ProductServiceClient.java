package dev.jbazann.skwidl.orders.order.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Validated
public interface ProductServiceClient {

    @NotNull CompletableFuture<Map<String, Object>> validateProductAndFetchCost(@NotNull List<Map<String, Object>> batch);
    @NotNull Boolean reserveProducts(@NotNull Map<UUID, @NotNull @Min(1) Integer> products);

}
