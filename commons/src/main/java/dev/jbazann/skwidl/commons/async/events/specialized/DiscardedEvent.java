package dev.jbazann.skwidl.commons.async.events.specialized;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data()
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DiscardedEvent extends DomainEvent {

    @NotNull @Valid
    private DomainEvent discarded;

    public static @NotNull @Valid DiscardedEvent discard(@NotNull @Valid DomainEvent event,
                                         @NotNull String context) {
        // Using copyOf() to avoid default init values, even if irrelevant.
        // The copied ones are at least related to the discarded event.
        final DiscardedEvent result = (DiscardedEvent) DomainEvent.copyOf(event)
                .context(context)
                .type(Type.DISCARD);
        return result.discarded(event);

    }

    public static DiscardedEvent discard(@NotNull @Valid DomainEvent event) {
        return DiscardedEvent.discard(event, "No action required.");
    }

        @Override
    protected DomainEvent copy() {
        return new DiscardedEvent()
                .discarded(discarded);
    }
}
