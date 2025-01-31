package dev.jbazann.skwidl.customers.site;

import dev.jbazann.skwidl.customers.site.exceptions.InvalidSiteStatusChangeException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteStatusService {

    private final CustomerServiceClient customerServiceLocalClient;

    public SiteStatusService(CustomerServiceClient customerServiceLocalClient) {
        this.customerServiceLocalClient = customerServiceLocalClient;
    }

    /**
     * Perform checks and operations to create a new site in its correct status.
     * @param site a valid Site instance.
     */
    @Transactional
    public void setInitialStatus(@NotNull @Valid Site site) {
        site.status(Site.SiteStatus.PENDING);
        if (customerServiceLocalClient.activateSite(site.customer(),site.id())) {
            site.status(Site.SiteStatus.ACTIVE);
        } else {
            customerServiceLocalClient.addPendingSite(site.customer());
        }
    }

    /**
     * If the operation is valid, orchestrate the change from whichever status the
     * referenced site is in, to the passed new status.
     * @param site a valid Site.
     * @param newStatus a status to (attempt to) set the site to.
     */
    @Transactional
    public void updateSiteStatus(@Valid @NotNull Site site, @NotNull Site.SiteStatus newStatus) {
        validateTransition(site, newStatus);
        switch(newStatus) { // TODO delegate to Spring state machine
            case ACTIVE -> transitionFromActive(site, newStatus);
            case PENDING -> transitionFromPending(site, newStatus);
        }
    }

    private void validateTransition(@NotNull Site site, @NotNull Site.SiteStatus newStatus) {
        if(!site.canTransitionTo(newStatus)) {
            throw new InvalidSiteStatusChangeException("Site status transition cannot be performed now.");
        }
    }

    @Transactional
    protected void transitionFromActive(Site site, Site.SiteStatus newStatus) {
        switch(newStatus) {
            case FINISHED -> customerServiceLocalClient.finishSite(site.customer(),site.id());
            case PENDING -> customerServiceLocalClient.deactivateSite(site.customer(),site.id());
        }
        site.status(newStatus);
    }

    @Transactional
    protected void transitionFromPending(Site site, Site.SiteStatus newStatus) {
        switch(newStatus) {
            case ACTIVE -> customerServiceLocalClient.activatePendingSite(site.customer(),site.id());
        }
        site.status(newStatus);
    }

}
