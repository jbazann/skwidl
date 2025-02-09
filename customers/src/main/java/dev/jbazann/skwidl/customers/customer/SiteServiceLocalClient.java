package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.site.SiteService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Lazy
@Service
public class SiteServiceLocalClient implements SiteServiceClient {

    private final SiteService siteService;

    public SiteServiceLocalClient(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * Signal {@link SiteService} to try and activate a {@link Customer}'s pending
     * sites.
     * @param customerId the customer ID to match sites for.
     */
    @Async
    public void signalActivatePendingSites(UUID customerId) {
        siteService.activatePendingSites(customerId);
    }

}
