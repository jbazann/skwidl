package dev.jbazann.skwidl.commons.async.transactions.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
    private boolean isCommitted;
    @NotNull
    private LocalDateTime expires;


    public static @NotNull @Valid CoordinatedTransaction from(@NotNull @Valid DomainEvent event) {
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
