package com.jbazann.orders.commons.async.transactions;

import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.identity.ApplicationMember;
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

    private List<ApplicationMember> quorum;
    private ApplicationMember coordinator;

    public boolean isMember(ApplicationMember member) {
        return quorum.contains(member);
    }

    public boolean isCoordinator(ApplicationMember member) {
        return coordinator == member;
    }

}
