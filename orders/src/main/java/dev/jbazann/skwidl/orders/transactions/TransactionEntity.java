package dev.jbazann.skwidl.orders.transactions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import dev.jbazann.skwidl.commons.utils.TimeProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@Document
public class TransactionEntity implements Transaction {

    @Id
    @NotNull @Valid
    private UUID id;
    @NotNull
    private LocalDateTime expires;
    @NotNull
    private TransactionStatus status;
    @NotNull @Valid
    private TransactionQuorum quorum;

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

}
