package com.jbazann.customers.site;

import com.jbazann.customers.site.exceptions.InvalidSiteStatusChangeException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteStatusService {

    private final SiteCallerService siteCallerService;

    @Autowired
    public SiteStatusService(SiteCallerService siteCallerService) {
        this.siteCallerService = siteCallerService;
    }

    /**
     * Perform checks and operations to create a new site in its correct status.
     * @param site a valid Site instance.
     */
    @Transactional
    public void setInitialStatus(@NotNull @Valid Site site) {
        site.status(Site.SiteStatus.PENDING);
        final boolean active = siteCallerService.activateSite(site.customer(),site.id());
        if(active) {
            site.status(Site.SiteStatus.ACTIVE);
        } else {
            siteCallerService.addPendingSite(site.customer());
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
            case ACTIVE: transitionFromActive(site, newStatus); break;
            case PENDING: transitionFromPending(site, newStatus); break;
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
            case FINISHED: siteCallerService.finishSite(site.customer(),site.id()); break;
            case PENDING: siteCallerService.deactivateSite(site.customer(),site.id()); break;
        }
        site.status(newStatus);
    }

    @Transactional
    protected void transitionFromPending(Site site, Site.SiteStatus newStatus) {
        switch(newStatus) {
            case ACTIVE: siteCallerService.activatePendingSite(site.customer(),site.id()); break;
        }
        site.status(newStatus);
    }

}
