package dev.jbazann.skwidl.customers.user.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
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
                .name(name)
                .lastname(lastname)
                .email(email)
                .dni(dni);
    }

}
