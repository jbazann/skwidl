package dev.jbazann.skwidl.customers.transaction;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@Entity
@Table(name = "transaction",schema = "transaction")
public class TransactionEntity implements Transaction {

    @Id
    @NotNull @Valid
    private UUID id;
    @NotNull
    private LocalDateTime expires;
    @NotNull
    private TransactionStatus status;
    @Embedded
    @NotNull @Valid
    private TransactionQuorumEmbeddable quorum;

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

    public Transaction quorum(TransactionQuorum quorum) {
        this.quorum = new TransactionQuorumEmbeddable(quorum);
        return this;
    }

}
