package dev.jbazann.skwidl.orders.order.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Validated
public interface CustomerServiceClient {

    Boolean billFor(@NotNull UUID id,@NotNull BigDecimal amount);
    CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(@NotNull UUID id);

}
