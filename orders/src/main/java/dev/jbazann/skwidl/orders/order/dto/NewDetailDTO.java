package dev.jbazann.skwidl.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.orders.order.entities.Detail;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
@Accessors(chain = true, fluent = true)
public class NewDetailDTO {

    private UUID product;
    private Integer amount;
    private BigDecimal discount;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    public Detail toEntity() {
        return new Detail()
                .product(product)
                .amount(amount)
                .discount(discount)
                .unitCost(unitCost)
                .totalCost(Detail.calculateTotalCost(amount, discount, unitCost));
    }

}
