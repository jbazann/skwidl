package dev.jbazann.skwidl.customers.site.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.site.Site;
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
public class SiteDTO {

    private UUID id;
    private String address;
    private String coordinates;
    private BigDecimal budget;
    private Site.SiteStatus status;
    private UUID customer;

    public Site toEntity() {
        return new Site(id, address, coordinates, budget, status, customer);
    }

}
