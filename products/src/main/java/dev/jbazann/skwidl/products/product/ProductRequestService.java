package dev.jbazann.skwidl.products.product;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProductRequestService {

    /**
     * Finds the first matching category ID for a given name.
     * @param name the category name to search for
     * @return the first match if found, null otherwise.
     */
    CompletableFuture<UUID> findCategoryByName(String name);
}
