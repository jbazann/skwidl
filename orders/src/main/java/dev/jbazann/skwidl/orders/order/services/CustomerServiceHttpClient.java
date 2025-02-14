package dev.jbazann.skwidl.orders.order.services;

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
    private Duration TIMEOUT = Duration.ofMillis(5000);
    @Value("${jbazann.routes.gateway.customers}")
    private String CUSTOMERS_ROOT = "NIL";
    @Value("${jbazann.routes.customers.v1.wallet.path}")
    private String CUSTOMERS_WALLET = "NIL";
    @Value("${jbazann.routes.customers.v1.wallet.params.operation.param}")
    private String OPERATION = "NIL";
    @Value("${jbazann.routes.customers.v1.wallet.params.operation.bill}")
    private String BILL = "NIL";
    @Value("${jbazann.routes.customers.v1.wallet.params.operation.credit}")
    private String CREDIT = "NIL";

    @Autowired
    public CustomerServiceHttpClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(UUID id) {
        return webClientBuilder.baseUrl(CUSTOMERS_ROOT).build().get()
                .uri(CUSTOMERS_WALLET,id)
                .retrieve().bodyToMono(BigDecimal.class).toFuture();
    }

    @Override
    public Boolean billFor(UUID id, BigDecimal amount) {
        return webClientBuilder.baseUrl(CUSTOMERS_ROOT).build().post()
                .uri(CUSTOMERS_WALLET,id)
                .attribute(OPERATION, BILL)
                .bodyValue(amount)
                .retrieve().bodyToMono(Boolean.class).block(TIMEOUT);
    }

}
