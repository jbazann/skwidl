package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.commons.exceptions.EntityNotFoundException;
import dev.jbazann.skwidl.commons.exceptions.UnexpectedResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public CustomerServiceRestClient(@Qualifier("loggingRestClientBuilder") RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(UUID id) {
        return CompletableFuture.supplyAsync(() ->
                restClientBuilder.baseUrl(CUSTOMERS_ROOT).build().get()
                        .uri(CUSTOMERS_WALLET,id)
                        .exchange((req, resp) -> {
                            if (resp.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                                throw new EntityNotFoundException(String.format(
                                        "Customer with id %s not found.", id
                                ));
                            }
                            if (!resp.getStatusCode().is2xxSuccessful()) {
                                throw new UnexpectedResponseException(
                                        "Validation failed with response code: " + resp.getStatusCode()
                                );
                            }
                            return resp.bodyTo(BigDecimal.class);
                        }));
    }

    @Override
    public Boolean billFor(UUID id, BigDecimal amount) {
        return restClientBuilder.baseUrl(CUSTOMERS_ROOT).build().post()
                .uri(CUSTOMERS_WALLET, id)
                .attribute(OPERATION, BILL)
                .body(amount)
                .exchange((req, resp) -> {
                    if (resp.getStatusCode().isSameCodeAs(HttpStatus.OK)) return true;
                    if (resp.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) return false;
                    if (resp.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                        throw new EntityNotFoundException(String.format(
                                "Customer with id %s not found.", id
                        ));
                    }
                    throw new UnexpectedResponseException(
                            "Billing failed with response code: " + resp.getStatusCode()
                    );
                });
    }

}
