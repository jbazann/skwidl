package dev.jbazann.skwidl.customers.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class NewUserDTO {

    @NotNull @NotEmpty
    private String name;
    @NotNull @NotEmpty
    private String lastname;
    @NotNull @NotEmpty @Email
    private String email;
    @NotNull @NotEmpty
    private String dni;

    public UserDTO toDto() {
        return new UserDTO()
                .setName(name)
                .setLastname(lastname)
                .setEmail(email)
                .setDni(dni);
    }

}
