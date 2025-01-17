package dev.jbazann.skwidl.commons.async.transactions.api.implement;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Accessors(chain = true, fluent = true)
public class CoordinatedTransaction {

    protected UUID id;
    protected Map<ApplicationMember, CoordinatedTransaction.QuorumStatus> quorumStatus;
    protected List<ApplicationMember> rollback;
    protected CoordinatedTransaction.TransactionStatus status;
    protected boolean isCommitted;
    protected LocalDateTime expires;


    public static CoordinatedTransaction from(DomainEvent event) {
        final Map<ApplicationMember, QuorumStatus> quorumStatus = new HashMap<>();
        event.transaction().quorum().members()
                .forEach(member -> quorumStatus.put(member, QuorumStatus.UNKNOWN));
        return new CoordinatedTransaction()
                .id(event.transaction().id())
                .status(TransactionStatus.NOT_PERSISTED)
                .quorumStatus(quorumStatus)
                .rollback(new ArrayList<>())
                .isCommitted(false)
                .expires(event.transaction().expires());
    }

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

    public CoordinatedTransaction addAccept(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.ACCEPT);
        return this;
    }

    public CoordinatedTransaction addCommit(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.COMMIT);
        return this;
    }

    public CoordinatedTransaction addReject(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.REJECT);
        return this;
    }

    public CoordinatedTransaction addRollback(ApplicationMember member) {
        rollback.add(member);
        return this;
    }

    public boolean isFullyRejected() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.REJECT ||
                        v == QuorumStatus.ROLLBACK);
    }

    public boolean isFullyCommitted() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.COMMIT);
    }

    public boolean isFullyAccepted() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.ACCEPT);
    }

    public boolean isRejected() {
        return TransactionStatus.REJECTED.equals(status);
    }

    public boolean isConcluded() {
        return switch (status) {
            case CONCLUDED_REJECT, CONCLUDED_COMMIT, CONCLUDED_EXPIRED -> true;
            case NOT_PERSISTED, STARTED, ACCEPTED, REJECTED, COMMITTED -> false;
        };
    }

    public enum QuorumStatus {
        UNKNOWN,
        ACCEPT,
        REJECT,
        COMMIT,
        ROLLBACK,
    }

    public enum TransactionStatus {
        NOT_PERSISTED,
        STARTED,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        CONCLUDED_EXPIRED,
        CONCLUDED_REJECT,
        CONCLUDED_COMMIT
    }

}
