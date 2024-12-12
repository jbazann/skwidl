package com.jbazann.orders.commons.async.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class DiscardedEvent extends DomainEvent {

    private DomainEvent discarded;

    public static DiscardedEvent from(DomainEvent event) {
        // TODO outdated
        return (DiscardedEvent) new DiscardedEvent()
                .discarded(event)
                .context("No action required.")
                .type(Type.DISCARD)
                .setRouting();
    }

}
