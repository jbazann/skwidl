package com.jbazann.customers.site;

import com.jbazann.customers.customer.CustomerService;
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
public class SiteCallerService {

    private CustomerService customerService;

    public void finishSite(UUID customer, UUID id) {
        customerService.finishSite(customer, id);
    }

    public void deactivateSite(UUID customer, UUID id) {
        customerService.deactivateSite(customer, id);
    }

    public void activatePendingSite(UUID customer, UUID id) {
        customerService.activatePendingSite(customer, id);
    }

    public void addPendingSite(UUID customer) {
        customerService.addPendingSite(customer);
    }

    public boolean activateSite(UUID customer, UUID id) {
        return customerService.activateSite(customer, id);
    }

    @Autowired
    public SiteCallerService setCustomerService(@Lazy CustomerService customerService) {
        this.customerService = customerService;
        return this;
    }
}
