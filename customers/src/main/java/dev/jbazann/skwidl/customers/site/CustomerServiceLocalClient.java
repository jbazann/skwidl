package dev.jbazann.skwidl.customers.site;

import dev.jbazann.skwidl.customers.customer.CustomerService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Convenience class to avoid circular dependencies when calling
 * services in other packages. It uses {@link Lazy} bean initialization and
 * setter injection to allow Spring to solve these loops.
 * There probably are many actual design patterns that solve
 * this, but I do not feel like refactoring.
 */
@Lazy
@Service
public class CustomerServiceLocalClient implements CustomerServiceClient {

    private CustomerService customerService;

    public void finishSite(@NotNull UUID customer, @NotNull UUID id) {
        customerService.finishSite(customer, id);
    }

    public void deactivateSite(@NotNull UUID customer,@NotNull UUID id) {
        customerService.deactivateSite(customer, id);
    }

    public void activatePendingSite(@NotNull UUID customer,@NotNull UUID id) {
        customerService.activatePendingSite(customer, id);
    }

    public void addPendingSite(@NotNull UUID customer) {
        customerService.addPendingSite(customer);
    }

    public boolean activateSite(@NotNull UUID customer,@NotNull UUID id) {
        return customerService.activateSite(customer, id);
    }

    @Autowired
    public CustomerServiceLocalClient setCustomerService(@Lazy CustomerService customerService) {
        this.customerService = customerService;
        return this;
    }
}
