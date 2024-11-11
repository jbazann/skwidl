package com.jbazann.orders.order.dto;

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
public class DetailDTO {

    private UUID id;
    private UUID product;
    private Integer amount;
    private BigDecimal discount;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

}
