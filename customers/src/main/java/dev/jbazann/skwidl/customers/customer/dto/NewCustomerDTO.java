package dev.jbazann.skwidl.customers.customer.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.customer.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
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
    private BigDecimal maxDebt;
    private Integer maxActiveSites;
    private List<UUID> enabledUsers;
    private List<UUID> enabledSites;
    private Integer pendingSites;

    public Customer toEntity() {
        return new Customer(id, name, email, cuit, maxDebt, maxActiveSites, enabledUsers, enabledSites, pendingSites);
    }

}
