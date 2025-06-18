package dev.jbazann.skwidl.customers.user.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
                .name(name)
                .lastname(lastname)
                .email(email)
                .dni(dni);
    }

}
