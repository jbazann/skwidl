package dev.jbazann.skwidl.customers.user;

import dev.jbazann.skwidl.customers.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> getCustomer(@RequestParam("id") UUID id,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("lastname") String lastname,
                                                     @RequestParam("email") String email,
                                                     @RequestParam("dni") String dni,
                                                     @RequestParam("customer") UUID customerId) {
        if (id == null && email == null && name == null && lastname == null && dni == null && customerId == null) {
            throw new IllegalArgumentException("Must provide at least one of: id, email, name, lastname, dni, customer.");
        }
        return ResponseEntity.ok(
                userService.findUsersByExample(
                        new User().id(id).name(name).lastname(lastname).email(email).dni(dni).customer(customerId)
                ).stream().map(User::toDto).toList()
        );
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        user.id(userService.generateUserId()); //TODO logic in controller
        return ResponseEntity.ok(userService.newUser(user.toEntity()).toDto());
    }


}
