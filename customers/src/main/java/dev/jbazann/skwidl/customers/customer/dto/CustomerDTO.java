package dev.jbazann.skwidl.customers.customer.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.customer.Customer;
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
public class CustomerDTO {

    private UUID id;
    private String name;
    private String email;
    private String cuit;
    private BigDecimal maxDebt;
    private BigDecimal budget;
    private Integer maxActiveSites;
    private List<UUID> enabledUsers;
    private List<UUID> enabledSites;
    private Integer pendingSites;


    public Customer toEntity() {
        return new Customer(id, name, email, cuit, maxDebt, budget, maxActiveSites, enabledUsers, enabledSites, pendingSites);
    }

}
