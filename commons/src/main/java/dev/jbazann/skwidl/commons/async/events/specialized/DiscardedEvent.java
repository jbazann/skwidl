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

    @Override
    protected DomainEvent copy() {
        return new DiscardedEvent()
                .discarded(discarded);
    }
    
}
