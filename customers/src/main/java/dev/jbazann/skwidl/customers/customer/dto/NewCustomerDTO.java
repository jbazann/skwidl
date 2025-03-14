package dev.jbazann.skwidl.customers.customer.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
public class NewCustomerDTO {

    private UUID id;
    @NotNull @Valid
    private String name;
    @NotNull @Valid
    private String email;
    @NotNull @Valid
    private String cuit;
    private List<UUID> enabledUsers;

    public CustomerDTO toDto() {
        return new CustomerDTO()
                .id(id)
                .name(name)
                .email(email)
                .cuit(cuit)
                .enabledUsers(enabledUsers == null ? new ArrayList<>() : enabledUsers);
    }

}
