package com.jbazann.orders.order.services;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CustomersRemoteServiceInterface {

    Boolean billFor(UUID id, BigDecimal amount);
    CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(UUID id);

}
