package dev.jbazann.skwidl.orders.order.dto;

import dev.jbazann.skwidl.orders.order.entities.Detail;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Accessors(chain = true, fluent = true)
public class DetailDTO {

    private UUID id;
    private UUID product;
    private Integer amount;
    private BigDecimal discount;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    public Detail toEntity() {
        return new Detail(id, product, amount, discount, unitCost, totalCost);
    }

}
