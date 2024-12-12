package com.jbazann.orders.commons.async.events;

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

    public static CancelAcceptedOrderEvent from(CancelAcceptedOrderEvent event) {
        return (CancelAcceptedOrderEvent) new CancelAcceptedOrderEvent()
                .orderId(event.orderId)
                .customerId(event.customerId)
                .returnedFunds(event.returnedFunds)
                .type(event.type())
                .context(event.context())
                .setRouting();
    }

}
