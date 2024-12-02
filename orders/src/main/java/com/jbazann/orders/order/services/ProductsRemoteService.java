package com.jbazann.orders.order.services;

import com.jbazann.orders.order.Sin;
import com.jbazann.orders.order.dto.ProductAmountDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
public class ProductsRemoteService implements ProductsRemoteServiceInterface {

    // Map<> keys expected by the external service
    // to be used when other classes must construct maps for batch operations
    public static final String PRODUCT_ID = "productId";
    public static final String REQUESTED_STOCK = "requestedStock";
    public static final String TOTAL_COST = "totalCost";
    public static final String PRODUCTS_EXIST = "productsExist";
    public static final String STOCK_AVAILABLE = "stockAvailable";

    private final WebClient.Builder webClientBuilder;

    @Value("${jbazann.timeout.standard}")
    private final Duration TIMEOUT = Duration.ofMillis(5000);

    @Value("${jbazann.gateway.products.base}")
    private final String PRODUCTS_BASE = "";

    @Value("${jbazann.gateway.products.validate}")
    private final String PRODUCTS_VALIDATE = "";

    @Value("${jbazann.gateway.products.reserve}")
    public final String PRODUCTS_RESERVE_STOCK = "";

    @Autowired
    public ProductsRemoteService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Start a new thread to synchronously request validation of a list of product IDs. This will additionally
     * fetch the unit cost of each product, and whether enough stock is available.
     * @param batch a list of {@link Map} instances where each item is equivalent to the JSON <p>
     * {
     *              {@link ProductsRemoteService#PRODUCT_ID}: {@link UUID},
     *              {@link ProductsRemoteService#REQUESTED_STOCK}: {@link Integer}
     * }
     * @return a {@link Map} containing at least the key {@link ProductsRemoteService#PRODUCTS_EXIST},
     * and potentially {@link ProductsRemoteService#TOTAL_COST}, {@link ProductsRemoteService#STOCK_AVAILABLE},
     * and one entry in the form {{@link UUID}: {@link BigDecimal}} per product ID in the list
     * received as argument.
     */
    @Async
    public CompletableFuture<Map<String, Object>> validateProductAndFetchCost(@NotNull List<Map<String, Object>> batch) {
        // verify input is well-formed
        if (!batch.stream().allMatch(m -> m.get(PRODUCT_ID) instanceof UUID && m.size() == 1))
            throw new IllegalArgumentException();// TODO exception messages
        // send request, sanitize response
        final WebClient webClient = webClientBuilder.baseUrl(PRODUCTS_BASE).build();
        return CompletableFuture.supplyAsync( () -> {
            final Map<String, Object> response = webClient.post().uri(PRODUCTS_VALIDATE).bodyValue(batch)
                    .retrieve().bodyToMono(new Sin()).block(TIMEOUT);
            return sanitizeValidatedProducts(Objects.requireNonNull(response));
        });
    }

    private Map<String, Object> sanitizeValidatedProducts(@NotNull Map<String, Object> response) {
        if( response.get(PRODUCTS_EXIST) instanceof Boolean productsExist ) {
            if(!productsExist) {
                // Return only the flag to prevent stupidity.
                Map<String, Object> sanitizedResponse = new HashMap<>();
                sanitizedResponse.put(PRODUCTS_EXIST, false);
                return sanitizedResponse;
            }
        } else {
            // TODO exceptions
        }

        if( response.get(TOTAL_COST) instanceof BigDecimal );
        if( response.get(STOCK_AVAILABLE) instanceof Boolean );
        return response;
    }


    public Boolean reserveProducts(Map<UUID, ProductAmountDTO> products) {
        return webClientBuilder.baseUrl(PRODUCTS_BASE).build()
                .post().uri(PRODUCTS_RESERVE_STOCK)
                .bodyValue(products.values().stream().collect(Collectors.toMap(ProductAmountDTO::id, ProductAmountDTO::amount)))
                .retrieve().bodyToMono(Boolean.class).block(TIMEOUT);
    }
}
