package dev.jbazann.skwidl.orders.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class NewDetailDTO {

    @NotNull
    private UUID product;
    @NotNull @Min(0)
    private Integer amount;
    private BigDecimal discount;
    private BigDecimal unitCost;

    public DetailDTO toDto() {
        return new DetailDTO()
                .setProduct(product)
                .setAmount(amount)
                .setDiscount(discount)
                .setUnitCost(unitCost);
    }

}
