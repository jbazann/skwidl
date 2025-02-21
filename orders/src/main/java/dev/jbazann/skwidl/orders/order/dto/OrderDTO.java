package dev.jbazann.skwidl.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.orders.order.entities.Order;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Order toEntity() {
        return new Order(id, ordered, orderNumber, user, customer, site, note,
                statusHistory.stream().map(StatusHistoryDTO::toEntity).collect(Collectors.toCollection(ArrayList::new)),
                totalCost,
                detail.stream().map(DetailDTO::toEntity).collect(Collectors.toCollection(ArrayList::new)));
    }

}
