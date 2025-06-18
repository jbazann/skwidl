package dev.jbazann.skwidl.orders.order.dto;

import dev.jbazann.skwidl.commons.utils.TimeProvider;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
@ToString
public class StatusHistoryDTO {

    private UUID id;
    private LocalDateTime date;
    private StatusHistory.Status status;
    private String detail;

    public StatusHistory toEntity() {
        return new StatusHistory(id, date == null ? TimeProvider.localDateTimeNow() : date, status, detail);
    }

}
