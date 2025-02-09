package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.dto.ProductAmountDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Validated
public interface ProductServiceClient {

    @NotNull CompletableFuture<@NotNull @NotEmpty Map<@NotEmpty String, @NotNull Object>> validateProductAndFetchCost(@NotNull List<Map<String, Object>> batch);
    @NotNull Boolean reserveProducts(@NotNull Map<UUID, @NotNull ProductAmountDTO> products);

}
