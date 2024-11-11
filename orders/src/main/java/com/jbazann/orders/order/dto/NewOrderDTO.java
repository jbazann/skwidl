package com.jbazann.orders.order.dto;

import com.jbazann.orders.order.entities.Detail;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class NewOrderDTO {

    private UUID user;
    private UUID customer;
    private UUID site;
    private String note;
    private List<NewDetailDTO> detail;

    public Order toEntity() {
        return new Order()
                .user(user)
                .customer(customer)
                .site(site)
                .note(note)
                .detail(detail.stream().map(NewDetailDTO::toEntity).toList());
    }

}
