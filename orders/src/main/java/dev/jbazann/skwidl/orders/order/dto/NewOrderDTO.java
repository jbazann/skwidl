package dev.jbazann.skwidl.orders.order.dto;

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
@ToString
@Accessors(chain = true)
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
                .setUser(user)
                .setCustomer(customer)
                .setSite(site)
                .setNote(note)
                .setDetail(detail.stream().map(NewDetailDTO::toDto).toList());
    }

}
