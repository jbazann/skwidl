package dev.jbazann.skwidl.commons.async.events.specialized;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data()
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeliverOrderEvent extends DomainEvent {

    @NotNull
    private UUID orderId;
    @NotNull
    private UUID customerId;
    @NotNull @Min(0)
    private BigDecimal returnedFunds;

    @Override
    protected DomainEvent copy() {
        return new DeliverOrderEvent()
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setReturnedFunds(returnedFunds);
    }
}
