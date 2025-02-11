package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.Locking;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.exceptions.LockAcquisitionException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Validated
public class TransactionLockingService {

    private final RedissonClient redisson;
    private final Map<UUID, TransactionLockingServiceData> currentLocks;// TODO concurrent data structures

    public TransactionLockingService(RedissonClient redisson) {
        this.redisson = redisson;
        this.currentLocks = new HashMap<>();
    }

    public void getLocks(@NotNull @Valid TransactionStage stage,
                         @NotNull @Valid DomainEvent event) {
        Optional<Locking> optional = getAnnotation(stage, event);
        if( optional.isEmpty() ) return;
        Locking annotation = optional.get();
        if (annotation.action().equals(Locking.LockingActions.GET))
            _getLocks(stage, event, annotation);
    }

    public void releaseLocks(@NotNull @Valid TransactionStage stage,
                             @NotNull @Valid DomainEvent event) {
        Optional<Locking> optional = getAnnotation(stage, event);
        if( optional.isEmpty() ) return;
        Locking annotation = optional.get();
        if (annotation.action().equals(Locking.LockingActions.RELEASE))
            _releaseLocks(event.transaction().id());
    }

    private void _getLocks(TransactionStage stage, DomainEvent event, Locking annotation) {
        switch (annotation.strategy()) {
            case EPHEMERAL -> getEphemeralLocks(stage, event, annotation);
            case MULTISTAGE -> throw new UnsupportedOperationException("Multi-stage locks unavailable.");
        }
    }

    private void _releaseLocks(UUID transactionId) {
        if (!currentLocks.containsKey(transactionId))
            throw new IllegalStateException("Method called without a lock to release.");
        TransactionLockingServiceData data = currentLocks.get(transactionId);
        data.acquiredLocks().forEach(RLock::unlock);
        currentLocks.remove(transactionId);
    }

    @Retryable(
            retryFor = {LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 250, multiplier = 2)
    )
    private void getEphemeralLocks(TransactionStage stage, DomainEvent event, Locking annotation) {
        TransactionLockingServiceData data;
        if (currentLocks.containsKey(event.transaction().id())) {
            data = currentLocks.get(event.transaction().id());
        } else {
            data = new TransactionLockingServiceData();
            List<EntityLock> entityLocks = stage.getRequiredLocks(event);
            data.locks(entityLocks.stream()
                    .map(EntityLock::toString)
                    .map(redisson::getLock)
                    .toList());
            currentLocks.put(event.transaction().id(), data);
        }

        data.acquiredLocks(data.locks().stream().filter(RLock::tryLock).toList());

        if(data.locks().size() != data.acquiredLocks().size()) {
            data.acquiredLocks().forEach(RLock::unlock);
            data.acquiredLocks(List.of());
            throw new LockAcquisitionException(String.format(
                    "Failed attempt %d for transaction %s.", data.retryCount(), data.transactionId()));
        }
    }

    private Optional<Locking> getAnnotation(TransactionStage stage, DomainEvent event) {
        if (currentLocks.containsKey(event.transaction().id())) {
            return Optional.of(currentLocks.get(event.transaction().id()).metadata());
        }
        if (stage.getClass().isAnnotationPresent(Locking.class)) {
            return Optional.of(stage.getClass().getAnnotation(Locking.class));
        }
        return Optional.empty();
    }

}
