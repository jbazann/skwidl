package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.site.SiteService;
import dev.jbazann.skwidl.customers.user.User;
import dev.jbazann.skwidl.customers.user.UserService;
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
public class CustomerCallerService {

    private SiteService siteService;
    private UserService userService;

    public void activatePendingSites(UUID customerId) {
        siteService.activatePendingSites(customerId);
    }

    public User addAllowedUser(UUID customerId, UUID userId) {
        return userService.addAllowedUser(customerId, userId);
    }

    @Autowired
    public CustomerCallerService setSiteOrchestratorService(@Lazy SiteService siteService) {
        this.siteService = siteService;
        return this;
    }

    @Autowired
    public CustomerCallerService setUserOrchestratorService(@Lazy UserService userService) {
        this.userService = userService;
        return this;
    }

}
