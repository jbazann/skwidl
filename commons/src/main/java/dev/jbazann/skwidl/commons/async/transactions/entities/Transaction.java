package dev.jbazann.skwidl.commons.async.transactions.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@RedisHash
public class Transaction {

    @Id
    @NotNull @Valid
    private UUID id;
    @NotNull
    private LocalDateTime expires;
    @NotNull
    private TransactionStatus status;
    @NotNull @Valid
    private TransactionQuorum quorum;

    public enum TransactionStatus {
        UNKNOWN,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        ROLLED_BACK,
        ERROR,
    }

    public static @NotNull @Valid Transaction from(@NotNull @Valid DomainEvent event) {
        return new Transaction()
                .id(event.transaction().id())
                .expires(event.transaction().expires())
                .quorum(event.transaction().quorum())
                .status(event.transaction().status());
    }

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }


}
