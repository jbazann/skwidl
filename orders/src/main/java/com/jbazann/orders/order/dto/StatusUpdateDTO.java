package com.jbazann.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.jbazann.orders.order.entities.StatusHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
public class StatusUpdateDTO {

    private StatusHistory.Status status;
    private String detail;

}
