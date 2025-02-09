package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Lazy
public class UserServiceLocalClient implements UserServiceClient {

    private final UserService userService;

    public UserServiceLocalClient(UserService userService) {
        this.userService = userService;
    }

    public void addAllowedUser(UUID customerId, UUID userId) {
        userService.addAllowedUser(customerId, userId);
    }

}
