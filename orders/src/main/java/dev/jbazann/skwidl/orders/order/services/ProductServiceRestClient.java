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
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductServiceRestClient implements ProductServiceClient {

    // Map<> keys expected by the external service
    // to be used when other classes must construct maps for batch operations
    public static final String PRODUCT_ID = "productId";
    public static final String REQUESTED_STOCK = "amount";
    public static final String TOTAL_COST = "totalCost";
    public static final String PRODUCTS_EXIST = "productsExist";
    public static final String UNIT_COST = "unitCost";

    private final RestClient.Builder restClientBuilder;

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
    public ProductServiceRestClient(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    /**
     * Start a new thread to synchronously request validation of a list of product IDs. This will additionally
     * fetch the unit cost of each product, and whether enough stock is available.
     * @param batch a list of {@link Map} instances where each item is equivalent to the JSON <p>
     * {
     *              {@link ProductServiceRestClient#PRODUCT_ID}: {@link UUID},
     *              {@link ProductServiceRestClient#REQUESTED_STOCK}: {@link Integer}
     * }
     * @return a {@link Map} containing at least the key {@link ProductServiceRestClient#PRODUCTS_EXIST},
     * and potentially {@link ProductServiceRestClient#TOTAL_COST} plus one entry in the form
     * {{@link UUID}: {@link BigDecimal}} per product ID in the list received as argument.
     */
    @Async
    public CompletableFuture<Map<String, Object>> validateProductAndFetchCost(List<Map<String, Object>> batch) {
        // verify input is well-formed
        if (!batch.stream().allMatch(m -> m.get(PRODUCT_ID) instanceof UUID && m.size() == 1))
            throw new IllegalArgumentException();// TODO exception messages
        // send request, sanitize response
        final RestClient restClient = restClientBuilder.baseUrl(PRODUCTS).build();
        return CompletableFuture.supplyAsync( () -> {
            final Map<String, Object> response = restClient.post()
                    .uri(builder -> builder.path(PRODUCTS_COLLECTION)
                            .queryParam(PRODUCTS_PARAMS_OPERATION, PRODUCTS_OPERATION_AVAILABILITY)
                            .build())
                    .body(batch)
                    .exchange((req, resp) -> {
                        if (!resp.getStatusCode().is2xxSuccessful()) {
                            throw new UnexpectedResponseException(
                                    "Product validation failed with response code: " + resp.getStatusCode()
                            );
                        }
                        return resp.bodyTo(new Sin());
                    });
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
        return restClientBuilder.baseUrl(PRODUCTS).build()
                .post().uri(builder -> builder.path(PRODUCTS_COLLECTION)
                        .queryParam(PRODUCTS_PARAMS_OPERATION, PRODUCTS_OPERATION_RESERVE)
                        .build())
                .body(products.values().stream().collect(Collectors.toMap(ProductAmountDTO::id, ProductAmountDTO::amount)))
                .exchange((req, resp) -> resp.getStatusCode().is2xxSuccessful());
    }
}
