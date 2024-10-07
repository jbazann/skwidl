package com.jbazann.customers.user.dto;

import com.jbazann.customers.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
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
