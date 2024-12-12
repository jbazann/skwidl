package com.jbazann.orders.commons.async.transactions;

import com.jbazann.orders.commons.identity.ApplicationMember;
import com.jbazann.orders.commons.utils.TimeProvider;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Accessors(fluent = true)
public class TransactionCoordinatorData {

    private UUID transactionId;
    private final Map<ApplicationMember, QuorumStatus> quorumStatus;
    private final List<ApplicationMember> rollback;
    private TransactionStatus transactionStatus;
    private boolean isCommitted;
    private LocalDateTime expires;

    public TransactionCoordinatorData() {
        this.transactionStatus = TransactionStatus.UNINITIALIZED;
        this.rollback = new ArrayList<>();
        this.quorumStatus = new HashMap<>();
        this.isCommitted = false;
    }

    public boolean expired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

    public TransactionCoordinatorData setQuorum(List<ApplicationMember> quorum) {
        quorum.forEach(m -> {
            quorumStatus.put(m, QuorumStatus.UNKNOWN);
        });
        return this;
    }


    public TransactionCoordinatorData accept(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.ACCEPT);
        return this;
    }

    public TransactionCoordinatorData commit(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.COMMIT);
        return this;
    }

    public TransactionCoordinatorData reject(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.REJECT);
        return this;
    }

    public TransactionCoordinatorData rollback(ApplicationMember member) {
        rollback.add(member);
        return this;
    }

    public boolean allRejected() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.REJECT ||
                        v == QuorumStatus.ROLLBACK);
    }

    public boolean allCommitted() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.COMMIT);
    }

    public boolean allAccepted() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.ACCEPT);
    }

    public boolean notRejected() {
        return transactionStatus != TransactionStatus.REJECTED;
    }

    public boolean concluded() {
        return switch (transactionStatus) {
            case CONCLUDED_REJECT, CONCLUDED_COMMIT, CONCLUDED_EXPIRED -> true;
            case UNINITIALIZED, STARTED, ACCEPTED, REJECTED, COMMITTED -> false;
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
        UNINITIALIZED,
        STARTED,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        CONCLUDED_EXPIRED,
        CONCLUDED_REJECT,
        CONCLUDED_COMMIT
    }

}
