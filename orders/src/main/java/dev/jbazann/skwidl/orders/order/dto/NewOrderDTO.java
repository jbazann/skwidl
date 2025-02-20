package dev.jbazann.skwidl.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.orders.order.entities.Order;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
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
