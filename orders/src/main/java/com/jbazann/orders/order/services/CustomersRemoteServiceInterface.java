package com.jbazann.orders.order.services;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CustomersRemoteServiceInterface {

    Boolean billFor(@NotNull UUID id,@NotNull BigDecimal amount);
    CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(UUID id);

}
