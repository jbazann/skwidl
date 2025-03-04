package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.commons.exceptions.UnexpectedResponseException;
import dev.jbazann.skwidl.orders.order.Sin;
import dev.jbazann.skwidl.orders.order.dto.ProductAmountDTO;
import dev.jbazann.skwidl.orders.order.exceptions.ReserveFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductServiceHttpClient implements ProductServiceClient {

    // Map<> keys expected by the external service
    // to be used when other classes must construct maps for batch operations
    public static final String PRODUCT_ID = "productId";
    public static final String REQUESTED_STOCK = "amount";
    public static final String TOTAL_COST = "totalCost";
    public static final String PRODUCTS_EXIST = "productsExist";
    public static final String UNIT_COST = "unitCost";

    private final WebClient.Builder webClientBuilder;

    @Value("${jbazann.timeout.standard}")
    private final Duration TIMEOUT = Duration.ofMillis(5000);

    @Value("${jbazann.routes.gateway.products}")
    private final String PRODUCTS = "";

    @Value("${jbazann.routes.products.v1.collection.path}")
    private final String PRODUCTS_COLLECTION = "";

    @Value("${jbazann.routes.products.v1.collection.params.operation.param}")
    private final String PRODUCTS_PARAMS_OPERATION = "";

    @Value("${jbazann.routes.products.v1.collection.params.operation.availability}")
    private final String PRODUCTS_OPERATION_AVAILABILITY = "";

    @Value("${jbazann.routes.products.v1.collection.params.operation.reserve}")
    private final String PRODUCTS_OPERATION_RESERVE = "";

    @Autowired
    public ProductServiceHttpClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Start a new thread to synchronously request validation of a list of product IDs. This will additionally
     * fetch the unit cost of each product, and whether enough stock is available.
     * @param batch a list of {@link Map} instances where each item is equivalent to the JSON <p>
     * {
     *              {@link ProductServiceHttpClient#PRODUCT_ID}: {@link UUID},
     *              {@link ProductServiceHttpClient#REQUESTED_STOCK}: {@link Integer}
     * }
     * @return a {@link Map} containing at least the key {@link ProductServiceHttpClient#PRODUCTS_EXIST},
     * and potentially {@link ProductServiceHttpClient#TOTAL_COST} plus one entry in the form
     * {{@link UUID}: {@link BigDecimal}} per product ID in the list received as argument.
     */
    @Async
    public CompletableFuture<Map<String, Object>> validateProductAndFetchCost(List<Map<String, Object>> batch) {
        // verify input is well-formed
        if (!batch.stream().allMatch(m -> m.get(PRODUCT_ID) instanceof UUID && m.size() == 1))
            throw new IllegalArgumentException();// TODO exception messages
        // send request, sanitize response
        final WebClient webClient = webClientBuilder.baseUrl(PRODUCTS).build();
        return CompletableFuture.supplyAsync( () -> {
            final Map<String, Object> response = webClient.post()
                    .uri(builder -> builder.path(PRODUCTS_COLLECTION)
                            .queryParam(PRODUCTS_PARAMS_OPERATION, PRODUCTS_OPERATION_AVAILABILITY)
                            .build())
                    .bodyValue(batch)
                    .retrieve().bodyToMono(new Sin()).block(TIMEOUT);
            return sanitizeValidatedProducts(Objects.requireNonNull(response));
        });
    }

    private Map<String, Object> sanitizeValidatedProducts(Map<String, Object> response) {
        if( response.get(PRODUCTS_EXIST) instanceof Boolean productsExist ) {
            if(!productsExist) {
                // Return only the flag to prevent stupidity.
                Map<String, Object> sanitizedResponse = new HashMap<>();
                sanitizedResponse.put(PRODUCTS_EXIST, false);
                return sanitizedResponse;
            }
        } else {
            throw new UnexpectedResponseException(String.format(
                    "%s attribute missing or malformed in Products service response.", PRODUCTS_EXIST
            ));
        }

        if( !(response.get(TOTAL_COST) instanceof BigDecimal) )
            throw new UnexpectedResponseException(String.format(
                    "%s attribute missing or malformed in Products service response.", TOTAL_COST
            ));
        if( !(response.get(UNIT_COST) instanceof Map<?,?> unitCost) )
            throw new UnexpectedResponseException(String.format(
                    "%s attribute missing or malformed in Products service response.", UNIT_COST
            ));

        unitCost.keySet().forEach(k -> response.put((String) k, unitCost.get(k)));

        return response;
    }


    public Boolean reserveProducts(Map<UUID, ProductAmountDTO> products) {
        return webClientBuilder.baseUrl(PRODUCTS).build()
                .post().uri(builder -> builder.path(PRODUCTS_COLLECTION)
                        .queryParam(PRODUCTS_PARAMS_OPERATION, PRODUCTS_OPERATION_RESERVE)
                        .build())
                .bodyValue(products.values().stream().collect(Collectors.toMap(ProductAmountDTO::id, ProductAmountDTO::amount)))
                .retrieve().onStatus(HttpStatusCode::is4xxClientError, (p) -> {
                    throw new ReserveFailureException();
                })
                .toBodilessEntity().thenReturn(true).block(TIMEOUT);
    }
}
