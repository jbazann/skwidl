package com.jbazann.orders.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class OrderDTO {

    private UUID id;
    private LocalDateTime ordered;
    private Long orderNumber;
    private UUID user;
    private UUID customer;
    private UUID site;
    private String note;
    private List<StatusHistoryDTO> statusHistory;
    private BigDecimal totalCost;
    private List<DetailDTO> detail;

}
