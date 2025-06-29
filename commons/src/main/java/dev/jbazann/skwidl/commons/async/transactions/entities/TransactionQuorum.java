package dev.jbazann.skwidl.commons.async.transactions.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * Represents a group of application services concerned by a {@link DomainEvent}.
 * Contains information about each service's role in the processing of the event, as well
 * as the completion state of said processing.
 */
@Data
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
public class TransactionQuorum {

    @NotNull @NotEmpty
    private List<@Valid ApplicationMember> members;
    @NotNull @Valid
    private ApplicationMember coordinator;

    public boolean isMember(@NotNull ApplicationMember member) {
        return members.contains(member);
    }

    public boolean isCoordinator(@NotNull ApplicationMember member) {
        return member.equals(coordinator);
    }

}
