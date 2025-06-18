package dev.jbazann.skwidl.commons.async.events.specialized;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data()
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CancelPreparedOrderEvent extends DomainEvent {

    @NotNull
    private UUID orderId;
    @NotNull
    private UUID customerId;
    @NotNull @Min(0)
    private BigDecimal returnedFunds;
    @NotNull @NotEmpty
    private Map<@NotNull UUID, @NotNull @Min(0) Integer> returnedStock;

    @Override
    protected DomainEvent copy() {
        return new CancelPreparedOrderEvent()
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setReturnedFunds(returnedFunds)
                .setReturnedStock(returnedStock);
    }

}
