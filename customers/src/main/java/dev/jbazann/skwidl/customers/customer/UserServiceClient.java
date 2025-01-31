package dev.jbazann.skwidl.customers.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserServiceClient {

    void addAllowedUser(@Valid @NotNull UUID customerId, @NotNull UUID userId);

}
