package dev.jbazann.skwidl.customers.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class NewUserDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String dni;

    public UserDTO toDto() {
        return new UserDTO()
                .setName(name)
                .setLastname(lastname)
                .setEmail(email)
                .setDni(dni);
    }

}
