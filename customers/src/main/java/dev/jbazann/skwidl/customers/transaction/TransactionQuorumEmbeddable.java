package dev.jbazann.skwidl.customers.transaction;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(fluent = true)
@Embeddable
public class TransactionQuorumEmbeddable extends TransactionQuorum {

    public TransactionQuorumEmbeddable(TransactionQuorum quorum) {
        members(quorum.members());
        coordinator(quorum.coordinator());
    }

}
