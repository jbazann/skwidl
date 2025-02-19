package dev.jbazann.skwidl.customers.user.dto;

import dev.jbazann.skwidl.customers.user.User;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@ToString
public class UserDTO {

    private UUID id;
    private String name;
    private String lastname;
    private String email;
    private String dni;
    private UUID customer;

    public User toEntity() {
        return new User(id, name, lastname, email, dni, customer);
    }

}
