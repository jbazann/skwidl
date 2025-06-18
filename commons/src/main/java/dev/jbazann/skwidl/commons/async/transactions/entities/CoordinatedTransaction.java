package dev.jbazann.skwidl.commons.async.transactions.entities;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Accessors(chain = true, fluent = true)
@ToString
@RedisHash
public class CoordinatedTransaction {

    @NotNull
    @Id private UUID id;
    @NotNull @NotEmpty
    private Map<ApplicationMember, CoordinatedTransaction.QuorumStatus> quorumStatus;
    @NotNull
    private List<ApplicationMember> rollback;
    @NotNull
    private CoordinatedTransaction.TransactionStatus status;
    @NotNull
    private LocalDateTime expires;


    public static @NotNull @Valid CoordinatedTransaction from(@NotNull @Valid DomainEvent event) {
        final Map<ApplicationMember, QuorumStatus> quorumStatus = new HashMap<>();
        event.transaction().quorum().members()
                .forEach(member -> quorumStatus.put(member, QuorumStatus.UNKNOWN));
        return new CoordinatedTransaction()
                .id(event.transaction().id())
                .status(TransactionStatus.STARTED)
                .quorumStatus(quorumStatus)
                .rollback(new ArrayList<>())
                .expires(event.transaction().expires());
    }

    public boolean isTimeExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }
    
    public @NotNull @Valid CoordinatedTransaction addAccept(@NotNull @Valid ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.ACCEPT);
        return this;
    }

    public @NotNull @Valid CoordinatedTransaction addCommit(@NotNull @Valid ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.COMMIT);
        return this;
    }

    public @NotNull @Valid CoordinatedTransaction addReject(@NotNull @Valid ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.REJECT);
        return this;
    }

    public @NotNull @Valid CoordinatedTransaction addRollback(@NotNull @Valid ApplicationMember member) {
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
                .allMatch(v -> v == QuorumStatus.ACCEPT || v == QuorumStatus.COMMIT);
    }

    public boolean isRejected() {
        return switch (status) {
            case CONCLUDED_REJECT, REJECTED, EXPIRED -> true;
            default -> false;
        };
    }

    public boolean isExpired() {
        return switch (status) {
            case EXPIRED, CONCLUDED_EXPIRED -> true;
            default -> false;
        };
    }

    public boolean isConcluded() {
        return switch (status) {
            case CONCLUDED_REJECT, CONCLUDED_COMMIT, CONCLUDED_EXPIRED -> true;
            default -> false;
        };
    }

    public boolean isCommitted() {
        return switch (status) {
            case COMMITTED, CONCLUDED_COMMIT -> true;
            default -> false;
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
        STARTED,
        REJECTED,
        COMMITTED,
        EXPIRED,
        CONCLUDED_EXPIRED,
        CONCLUDED_REJECT,
        CONCLUDED_COMMIT
    }

}
