package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.transactions.api.Locking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.redisson.api.RLock;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLockingServiceData {

    private UUID transactionId;
    private List<RLock> locks;
    private List<RLock> acquiredLocks;
    private Locking metadata;
    private int retries = 1; // this is a counter

}
