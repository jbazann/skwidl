package com.jbazann.orders.order.commons.async.transactions.data;

import com.jbazann.commons.async.transactions.data.CoordinatedTransaction;
import com.jbazann.commons.async.transactions.data.TransientCoordinatedTransaction;
import com.jbazann.commons.identity.ApplicationMember;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode
@Accessors(chain = true, fluent = true)
@Document(collection="coordinated-transaction")
public class PersistentCoordinatedTransaction implements CoordinatedTransaction {

    private UUID id;
    private Map<ApplicationMember, TransientCoordinatedTransaction.QuorumStatus> quorumStatus;
    private List<ApplicationMember> rollback;
    private TransientCoordinatedTransaction.TransactionStatus status;
    private boolean isCommitted;
    private LocalDateTime expires;

}
