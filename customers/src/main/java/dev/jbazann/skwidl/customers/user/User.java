package dev.jbazann.skwidl.customers.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.user.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@Entity
@Table(name = "user", schema = "user")
public class User {

    @Id
    @GeneratedValue
    @NotNull
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String lastname;
    @NotNull @Email
    private String email;
    @NotNull
    private String dni;
    @NotNull
    private UUID customer;

    public UserDTO toDto() {
        return new UserDTO(id, name, lastname, email, dni, customer);
    }

    public boolean enabledForCustomerId(@NotNull UUID id) {
        return customer.equals(id);
    }

}
