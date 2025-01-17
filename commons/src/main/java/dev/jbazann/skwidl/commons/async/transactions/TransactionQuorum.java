package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * Represents a group of application services concerned by a {@link DomainEvent}.
 * Contains information about each service's role in the processing of the event, as well
 * as the completion state of said processing.
 */
@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public final class TransactionQuorum {

    private List<ApplicationMember> members;
    private ApplicationMember coordinator;

    public boolean isMember(ApplicationMember member) {
        return members.contains(member);
    }

    public boolean isCoordinator(ApplicationMember member) {
        return coordinator == member;
    }

}
