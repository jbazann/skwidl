package dev.jbazann.skwidl.commons.async.events.specialized;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data()
@Accessors(chain = true, fluent = true)
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
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds);
    }
}
