package com.jbazann.customers.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerUserService {

    /**
     * Add allowed user if it's not already present. No user ID validation will be performed (so
     * invalid references may exist).
     * @param customer a valid Customer.
     * @param userId a user ID that is presumed, but not required to be valid.
     */
    public void addAllowedUser(@Valid @NotNull Customer customer, @NotNull UUID userId) {
        if(hasAllowedUser(customer, userId)) {
            // TODO log, user already allowed
            return;
        }
        customer.addAllowedUser(userId);
    }

    private boolean hasAllowedUser(@NotNull Customer customer, @NotNull UUID userId) {
        return customer.allowedUsers().contains(userId);
    }

}
