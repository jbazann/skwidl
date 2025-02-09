package dev.jbazann.skwidl.customers.site;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface CustomerServiceClient {

    void finishSite(@NotNull UUID customer,@NotNull UUID id);

    void deactivateSite(@NotNull UUID customer,@NotNull  UUID id);

    void activatePendingSite(@NotNull UUID customer,@NotNull  UUID id);

    void addPendingSite(@NotNull UUID customer);

    boolean activateSite(@NotNull UUID customer,@NotNull  UUID id);

}
