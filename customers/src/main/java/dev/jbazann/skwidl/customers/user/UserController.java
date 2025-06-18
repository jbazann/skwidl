package dev.jbazann.skwidl.customers.user;

import dev.jbazann.skwidl.customers.user.dto.NewUserDTO;
import dev.jbazann.skwidl.customers.user.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getCustomer(@RequestParam("id") UUID id,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("lastname") String lastname,
                                                     @RequestParam("email") String email,
                                                     @RequestParam("dni") String dni,
                                                     @RequestParam("customer") UUID customerId) {
        if (id == null && email == null && name == null && lastname == null && dni == null && customerId == null) {
            throw new IllegalArgumentException("Must provide at least one of: id, email, name, lastname, dni, customer.");
        }
        return userService.findUsersByExample(
                new User().setId(id).setName(name).setLastname(lastname).setEmail(email).setDni(dni)
                        .setCustomers(customerId == null ? null : List.of(customerId))
        ).stream().map(User::toDto).toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody NewUserDTO user) {
        return userService.newUser(user).toDto();
    }


}
