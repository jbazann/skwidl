package com.jbazann.orders.order.entities;

import com.jbazann.orders.order.OnValidated;
import com.jbazann.orders.order.dto.OrderDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Accessors(chain = true, fluent = true)
@Document(collection="order")
public class Order {

    @MongoId(FieldType.STRING)
    @NotNull
    @NotNull
    private UUID id;
    @NotNull
    private LocalDateTime ordered;
    @Indexed(unique=true)
    @NotNull
    private Long orderNumber;
    @NotNull
    private UUID user;
    @NotNull
    private UUID customer;
    @NotNull
    private UUID site;
    @NotNull
    private String note;
    @NotNull @NotEmpty
    private List<@Valid StatusHistory> statusHistory;
    @Field(targetType = FieldType.DECIMAL128)
    @NotNull @Min(0)
    private BigDecimal totalCost;
    @NotNull @NotEmpty
    private List<@Valid Detail> detail;

    public Order setStatus(@NotNull @Valid StatusHistory newStatus) {
        if(statusHistory == null) {
            statusHistory = new ArrayList<>();
        }
        statusHistory.add(newStatus);
        return this;
    }

    @OnValidated
    public StatusHistory getStatus() {
        return statusHistory.getLast();
    }

    public OrderDTO toDto() {
        return new OrderDTO(id, ordered, orderNumber, user, customer, site, note,
                statusHistory == null ? null : statusHistory.stream().map(StatusHistory::toDto).toList(),
                totalCost,
                detail == null ? null : detail.stream().map(Detail::toDto).toList());
    }

}
