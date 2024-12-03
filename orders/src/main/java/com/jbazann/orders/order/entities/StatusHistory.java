package com.jbazann.orders.order.entities;

import com.jbazann.orders.order.dto.StatusHistoryDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Document
public class StatusHistory {

    @MongoId(FieldType.STRING)
    @NotNull
    private UUID id;
    @NotNull
    private LocalDateTime date;
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
