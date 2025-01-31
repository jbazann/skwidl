package dev.jbazann.skwidl.customers.site;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface CustomerServiceClient {

    public void finishSite(@NotNull UUID customer,@NotNull UUID id);

    public void deactivateSite(@NotNull UUID customer,@NotNull  UUID id);

    public void activatePendingSite(@NotNull UUID customer,@NotNull  UUID id);

    public void addPendingSite(@NotNull UUID customer);

    public boolean activateSite(@NotNull UUID customer,@NotNull  UUID id);

}
