package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    public void addAllowedUser(@Valid @NotNull UUID customerId, @NotNull UUID userId) {
        userService.addAllowedUser(customerId, userId);
    }

}
