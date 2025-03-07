package dev.jbazann.skwidl.orders.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomerServiceRestClient implements CustomerServiceClient {

    private final RestClient.Builder restClientBuilder;

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
    public CustomerServiceRestClient(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(UUID id) {
        return CompletableFuture.supplyAsync(() ->
                restClientBuilder.baseUrl(CUSTOMERS_ROOT).build().get()
                        .uri(CUSTOMERS_WALLET,id)
                        .retrieve()
                        .body(BigDecimal.class));
    }

    @Override
    public Boolean billFor(UUID id, BigDecimal amount) {
        return restClientBuilder.baseUrl(CUSTOMERS_ROOT).build().post()
                .uri(CUSTOMERS_WALLET, id)
                .attribute(OPERATION, BILL)
                .body(amount)
                .retrieve()
                .body(Boolean.class);
    }

}
