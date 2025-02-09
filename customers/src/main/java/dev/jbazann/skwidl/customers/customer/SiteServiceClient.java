package dev.jbazann.skwidl.customers.customer;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface SiteServiceClient {

    void signalActivatePendingSites(@NotNull UUID customerId);

}
