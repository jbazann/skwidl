package dev.jbazann.skwidl.customers.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface UserServiceClient {

    void addAllowedUser(@Valid @NotNull UUID customerId, @NotNull UUID userId);

}
