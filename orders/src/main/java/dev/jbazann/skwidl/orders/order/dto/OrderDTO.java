package dev.jbazann.skwidl.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
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
