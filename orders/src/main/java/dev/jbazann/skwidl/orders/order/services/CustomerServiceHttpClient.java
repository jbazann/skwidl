package dev.jbazann.skwidl.orders.order.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomerServiceHttpClient implements CustomerServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${jbazann.timeout.standard}")
    private final Duration TIMEOUT = Duration.ofMillis(5000);
    @Value("${jbazann.gateway.customers.base}")
    private final String CUSTOMERS_BASE = "";
    @Value("${jbazann.gateway.customers.budget}")
    private final String CUSTOMERS_BUDGET = "";
    @Value("${jbazann.gateway.customers.bill}")
    private final String CUSTOMERS_BILL = "";
    @Value("${jbazann.gateway.customers.credit}")
    private final String CUSTOMERS_CREDIT = "";

    @Autowired
    public CustomerServiceHttpClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(@NotNull UUID id) {
        return webClientBuilder.baseUrl(CUSTOMERS_BASE).build().get()
                .uri(CUSTOMERS_BUDGET,id)
                .retrieve().bodyToMono(BigDecimal.class).toFuture();
    }

    public Boolean billFor(@NotNull UUID id,@NotNull BigDecimal amount) {
        return webClientBuilder.baseUrl(CUSTOMERS_BASE).build().post()
                .uri(CUSTOMERS_BILL,id,amount)
                .retrieve().bodyToMono(Boolean.class).block(TIMEOUT);
    }

}
