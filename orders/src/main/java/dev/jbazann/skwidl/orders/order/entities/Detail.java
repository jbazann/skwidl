package dev.jbazann.skwidl.orders.order.entities;

import dev.jbazann.skwidl.orders.order.dto.DetailDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Accessors(chain = true)
@ToString
@Document
public class Detail {

    @MongoId(FieldType.STRING)
    @NotNull
    private UUID id;
    @NotNull
    private UUID product;
    @NotNull @Min(0)
    private Integer amount;
    @Field(targetType = FieldType.DECIMAL128)
    @NotNull @Min(0) @Max(100)
    private BigDecimal discount;
    @Field(targetType = FieldType.DECIMAL128)
    @NotNull @Min(0)
    private BigDecimal unitCost;
    @Field(targetType = FieldType.DECIMAL128)
    @NotNull @Min(0)
    private BigDecimal totalCost;


    public static BigDecimal calculateTotalCost(@NotNull Integer amount,
                                                @NotNull BigDecimal unitCost,
                                                @NotNull BigDecimal discount) {
        // (units * unitCost)*(1-discount/100)
        return (unitCost.multiply(BigDecimal.valueOf(amount)))
                .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), RoundingMode.CEILING)));
    }

    public DetailDTO toDto() {
        return new DetailDTO(id, product, amount, discount, unitCost, totalCost);
    }

}
