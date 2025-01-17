package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.site.SiteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerSiteService {

    private final CustomerCallerService customerCallerService;

    public CustomerSiteService(CustomerCallerService customerCallerService) {
        this.customerCallerService = customerCallerService;
    }

    /**
     * Register the given site as active, if the customer's limits allow it.
     * @param customer a valid Customer instance.
     * @param siteId a site ID that is presumed, but not required to be valid.
     * @return whether the site was registered as active by the customer instance.
     */
    public boolean activateSite(@Valid @NotNull Customer customer, @NotNull UUID siteId) {
        if(canActivateSites(customer)){
            customer.addActiveSite(siteId);
            return true;
        }
        return false;
    }

    /**
     * {@link CustomerSiteService#activateSite(Customer, UUID)} wrapper for sites that were
     * previously registered as pending on the customer.
     * @param customer a valid Customer instance.
     * @param siteId a site ID that is presumed, but not required to be valid.
     */
    public void activatePendingSite(@Valid @NotNull Customer customer, @NotNull UUID siteId) {
        if (canActivateSites(customer)) {
            activateSite(customer, siteId);
            if (hasPendingSites(customer)) {
                customer.removePendingSite();
            } else {
                // TODO log, customer unaware of pending site
            }
        }
    }

    /**
     * Remove the given site from {@link Customer#activeSites()}.
     * @param customer a valid Customer instance.
     * @param siteId a site ID that is presumed, but not required to be valid.
     */
    public void finishSite(@NotNull Customer customer, @NotNull UUID siteId) {
        if(!hasActiveSite(customer, siteId)) {
            // TODO log, customer unaware of active site
            return;
        }
        customer.removeActiveSite(siteId);
        if(hasPendingSites(customer)) signalActivatePendingSites(customer.id());
    }

    /**
     * Signal {@link SiteService} to try and activate a {@link Customer}'s pending
     * sites.
     * @param customerId the customer ID to match sites for.
     */
    @Async
    protected void signalActivatePendingSites(@NotNull UUID customerId) {
        customerCallerService.activatePendingSites(customerId);
    }

    /**
     * Like {@link CustomerService#finishSite(UUID, UUID)}, remove the given site from
     * {@link Customer#activeSites()}, but also increment {@link Customer#pendingSites()}.
     * @param customer a valid Customer instance.
     * @param siteId a site ID that is presumed, but not required to be valid.
     */
    public void deactivateSite(@NotNull Customer customer, @NotNull UUID siteId) {
        if(!hasActiveSite(customer, siteId)) {
            //TODO log, site assumed active, customer unaware
        }
        customer.removeActiveSite(siteId).countPendingSite();
    }

    private boolean canActivateSites(@NotNull Customer customer) {
        return customer.activeSites().size() < customer.maxActiveSites();
    }

    private boolean hasPendingSites(@NotNull Customer customer) {
        return customer.pendingSites() > 0;
    }

    private boolean hasActiveSite(@NotNull Customer customer, @NotNull UUID siteId) {
        return customer.activeSites().contains(siteId);
    }

}
