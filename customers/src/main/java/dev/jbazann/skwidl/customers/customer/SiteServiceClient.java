package dev.jbazann.skwidl.customers.customer;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface SiteServiceClient {

    void signalActivatePendingSites(@NotNull UUID customerId);

}
