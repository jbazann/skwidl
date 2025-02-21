package dev.jbazann.skwidl.customers.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.user.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
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
    @NotNull
    private UUID id;
    @NotNull @NotEmpty
    private String name;
    @NotNull @NotEmpty
    private String lastname;
    @NotNull @NotEmpty @Email
    private String email;
    @NotNull @NotEmpty
    private String dni;
    @ElementCollection
    @NotNull
    private List<@NotNull UUID> customers;

    public UserDTO toDto() {
        return new UserDTO(id, name, lastname, email, dni, customers);
    }

    public boolean enabledForCustomerId(@NotNull UUID id) {
        return customers.contains(id);
    }

}
