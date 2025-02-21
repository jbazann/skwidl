package dev.jbazann.skwidl.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
public class StatusHistoryDTO {

    private UUID id;
    private LocalDateTime date;
    private StatusHistory.Status status;
    private String detail;

    public StatusHistory toEntity() {
        return new StatusHistory(id, date, status, detail);
    }

}
