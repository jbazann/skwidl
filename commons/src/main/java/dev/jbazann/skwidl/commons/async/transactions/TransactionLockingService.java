package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.Locking;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.exceptions.LockAcquisitionException;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Validated
public class TransactionLockingService {

    private final RedissonClient redisson;
    private final ConcurrentMap<UUID, TransactionLockingServiceData> currentLocks;
    private final Logger log = LoggerFactory.get(TransactionLockingService.class);

    public TransactionLockingService(RedissonClient redisson) {
        this.redisson = redisson;
        this.currentLocks = new ConcurrentHashMap<>();
    }

    public void getLocks(@NotNull @Valid TransactionStage stage,
                         @NotNull @Valid DomainEvent event) {
        log.method(stage,event);
        Optional<Locking> optional = getAnnotation(stage, event);
        if( optional.isEmpty() ) {
            log.debug("%s annotation not found.", Locking.class.getSimpleName());
            return;
        }
        Locking annotation = optional.get();
        if (annotation.action().equals(Locking.LockingActions.GET)) {
            _getLocks(stage, event, annotation);
        } else {
            log.debug("getLocks called for action %s", annotation.action());
        }
    }

    public void releaseLocks(@NotNull @Valid TransactionStage stage,
                             @NotNull @Valid DomainEvent event) {
        log.method(stage,event);
        Optional<Locking> optional = getAnnotation(stage, event);
        if( optional.isEmpty() ) {
            log.debug("%s annotation not found.", Locking.class.getSimpleName());
            return;
        }
        Locking annotation = optional.get();
        if (annotation.action().equals(Locking.LockingActions.RELEASE)) {
            _releaseLocks(event.transaction().id());
        } else {
            log.debug("releaseLocks called for action %s", annotation.action());
        }
    }

    private void _getLocks(TransactionStage stage, DomainEvent event, Locking annotation) {
        log.method(stage,event,annotation);
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
        log.method(stage,event,annotation);
        log.debug("Current locks %s.", currentLocks);
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
            currentLocks.put(event.transaction(
            ).id(), data);
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
