package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.commons.exceptions.UnexpectedResponseException;
import dev.jbazann.skwidl.orders.order.Sin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private String PRODUCTS = "NIL";
    @Value("${jbazann.routes.products.v1.collection.path}")
    private String PRODUCTS_COLLECTION = "NIL";
    @Value("${jbazann.routes.products.v1.collection.params.operation.param}")
    private String PRODUCTS_PARAMS_OPERATION = "NIL";
    @Value("${jbazann.routes.products.v1.collection.params.operation.availability}")
    private String PRODUCTS_OPERATION_AVAILABILITY = "NIL";
    @Value("${jbazann.routes.products.v1.collection.params.operation.reserve}")
    private String PRODUCTS_OPERATION_RESERVE = "NIL";

    @Autowired
    public ProductServiceRestClient(@Qualifier("loggingRestClientBuilder") RestClient.Builder restClientBuilder) {
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
        if (!batch.stream().allMatch(m -> m.get(PRODUCT_ID) instanceof UUID &&
                m.get(REQUESTED_STOCK) instanceof Integer &&
                m.size() == 2))
            throw new IllegalArgumentException(batch.stream().map(Map::toString).reduce("",(a,b) -> a +" ;; "+b));// TODO exception messages
        // send request, sanitize response
        final RestClient restClient = restClientBuilder.baseUrl(PRODUCTS).build();
        return CompletableFuture.supplyAsync( () -> {
            final Map<String, Object> response = restClient.post()
                    .uri(builder -> builder.path(PRODUCTS_COLLECTION)
                            .queryParam(PRODUCTS_PARAMS_OPERATION, PRODUCTS_OPERATION_AVAILABILITY)
                            .build())
                    .body(batch)
                    .retrieve()
                    .body(new Sin());
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

        // TOTAL_COST cannot be type-checked for some reason I am not willing to dig into.
        // I assume BigDecimal objects are being serialized as either String or float,
        // thus failing instanceof BigDecimal checks when deserialized as Object.
        // This entire call stack is already (intentionally) way off a correct implementation,
        // so I will not address these edge cases any further.

        if( !(response.get(UNIT_COST) instanceof Map<?,?> unitCost) )
            throw new UnexpectedResponseException(String.format(
                    "%s attribute missing or malformed in Products service response.", UNIT_COST
            ));

        unitCost.keySet().forEach(k -> response.put((String) k, unitCost.get(k)));

        return response;
    }


    public Boolean reserveProducts(List<Map<String, Object>> products) {
        return restClientBuilder.baseUrl(PRODUCTS).build()
                .post().uri(builder -> builder.path(PRODUCTS_COLLECTION)
                        .queryParam(PRODUCTS_PARAMS_OPERATION, PRODUCTS_OPERATION_RESERVE)
                        .build())
                .body(products)
                .exchange((req, resp) -> resp.getStatusCode().is2xxSuccessful());
    }
}
