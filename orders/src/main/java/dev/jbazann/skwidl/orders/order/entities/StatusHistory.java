package dev.jbazann.skwidl.orders.order.entities;

import dev.jbazann.skwidl.commons.utils.TimeProvider;
import dev.jbazann.skwidl.orders.order.dto.StatusHistoryDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
@ToString
@Document
public class StatusHistory {

    @MongoId(FieldType.STRING)
    @NotNull
    private UUID id;
    @NotNull
    private LocalDateTime date = TimeProvider.localDateTimeNow(); // TODO factory or something
    @NotNull
    private Status status;
    @NotNull
    private String detail;

    public enum Status {
        REJECTED, IN_PREPARATION, ACCEPTED, CANCELED, DELIVERED
    }

    public StatusHistoryDTO toDto() {
        return new StatusHistoryDTO(id, date, status, detail);
    }

}
