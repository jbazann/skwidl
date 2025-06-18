package dev.jbazann.skwidl.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.orders.order.entities.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private UUID user;
    @NotNull
    private UUID customer;
    @NotNull
    private UUID site;
    @NotEmpty
    private String note;
    @NotNull @NotEmpty
    private List<@NotNull @Valid NewDetailDTO> detail;

    public OrderDTO toDto() {
        return new OrderDTO()
                .user(user)
                .customer(customer)
                .site(site)
                .note(note)
                .detail(detail.stream().map(NewDetailDTO::toDto).toList());
    }

}
