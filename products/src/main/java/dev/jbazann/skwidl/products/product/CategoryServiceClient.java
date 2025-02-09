package dev.jbazann.skwidl.products.product;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Validated
public interface CategoryServiceClient {

    /**
     * Finds the first matching category ID for a given name.
     * @param name the category name to search for
     * @return the first match if found, null otherwise.
     */
    CompletableFuture<UUID> findCategoryByName(@NotNull String name);
}
