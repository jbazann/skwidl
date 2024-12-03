package com.jbazann.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
public class DetailDTO {

    private UUID id;
    private UUID product;
    private Integer amount;
    private BigDecimal discount;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

}
