package dev.jbazann.skwidl.orders.order.dto;

import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class StatusUpdateDTO {

    private StatusHistory.Status status;
    private String detail;

}
