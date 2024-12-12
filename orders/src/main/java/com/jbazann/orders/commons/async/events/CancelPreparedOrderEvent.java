package com.jbazann.orders.commons.async.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class CancelPreparedOrderEvent extends DomainEvent {

    private UUID orderId;
    private UUID customerId;
    private BigDecimal returnedFunds;
    private Map<UUID, Integer> returnedStock;

    public static CancelPreparedOrderEvent from(CancelPreparedOrderEvent event) {
        return (CancelPreparedOrderEvent) new CancelPreparedOrderEvent()
                .orderId(event.orderId)
                .customerId(event.customerId)
                .returnedFunds(event.returnedFunds)
                .returnedStock(event.returnedStock)
                .type(event.type())
                .context(event.context())
                .setRouting();
    }

}
