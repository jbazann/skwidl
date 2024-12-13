package com.jbazann.orders.commons.async.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class DiscardedEvent extends DomainEvent {

    private DomainEvent discarded;

    public static DiscardedEvent discard(DomainEvent event, String context) {
        // Using copyOf() to avoid default init values, even if irrelevant.
        // The copied ones are at least related to the discarded event.
        final DiscardedEvent result = (DiscardedEvent) DomainEvent.copyOf(event)
                .context(context)
                .type(Type.DISCARD);
        return result.discarded(event);

    }

    public static DiscardedEvent discard(DomainEvent event) {
        return DiscardedEvent.discard(event, "No action required.");
    }

        @Override
    protected DomainEvent copy() {
        return new DiscardedEvent()
                .discarded(discarded);
    }
}
