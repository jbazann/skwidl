package dev.jbazann.skwidl.commons.async.events.specialized;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class CancelAcceptedOrderEvent extends DomainEvent {

    private UUID orderId;
    private UUID customerId;
    private BigDecimal returnedFunds;

    @Override
    protected DomainEvent copy() {
        return new CancelAcceptedOrderEvent()
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds);
    }
    
}
