package dev.jbazann.skwidl.customers.site.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
public class NewSiteDTO {

    @NotBlank
    private String address;
    @NotEmpty
    private String coordinates;
    @Min(0)
    private BigDecimal budget;
    @NotNull
    private UUID customer;

    public SiteDTO toDto() {
        return new SiteDTO()
                .address(address)
                .customer(customer)
                .budget(budget)
                .coordinates(coordinates);
    }

}
