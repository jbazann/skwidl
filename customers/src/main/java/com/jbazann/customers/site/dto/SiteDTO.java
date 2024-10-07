package com.jbazann.customers.site.dto;

import com.jbazann.customers.site.Site;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
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
