package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.transactions.api.locking.Locking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLockingServiceData {

    @NotNull
    private UUID transactionId;
    @NotNull
    private List<RLock> locks;
    @NotNull
    private List<RLock> acquiredLocks;
    @NotNull @Valid
    private Locking metadata;
    @Min(0)
    private int retryCount = 1;

}
