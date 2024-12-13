package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.events.DomainEvent;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TransactionPhaseRegistrar {

    private final Map<Class<? extends DomainEvent>, TransactionPhase> reservePhase;
    private final Map<Class<? extends DomainEvent>, TransactionPhase> commitPhase;
    private final Map<Class<? extends DomainEvent>, TransactionPhase> rollbackPhase;

    public TransactionPhaseRegistrar(ApplicationContext applicationContext) {
        reservePhase = buildOperationMap(applicationContext.getBeansWithAnnotation(ReservePhase.class));
        commitPhase = buildOperationMap(applicationContext.getBeansWithAnnotation(CommitPhase.class));
        rollbackPhase = buildOperationMap(applicationContext.getBeansWithAnnotation(RollbackPhase.class));
    }

    public Optional<TransactionPhase> getReservePhaseFor(DomainEvent event) {
        return getForPhase(reservePhase, event);
    }

    public Optional<TransactionPhase> getCommitPhaseFor(DomainEvent event) {
        return getForPhase(commitPhase, event);
    }

    public Optional<TransactionPhase> getRollbackPhaseFor(DomainEvent event) {
        return getForPhase(rollbackPhase, event);
    }

    private Optional<TransactionPhase> getForPhase(Map<Class<? extends DomainEvent>, TransactionPhase> phaseBeans, DomainEvent event) {
        final Class<? extends DomainEvent> eventClass = event.getClass();
        if (!phaseBeans.containsKey(eventClass)) return Optional.empty();
        return Optional.of(phaseBeans.get(eventClass));
    }

    private static Map<Class<? extends DomainEvent>, TransactionPhase> buildOperationMap(Map<String, Object> beans) {
        return beans.values().stream()
                .filter(TransactionPhaseRegistrar::isTransactionPhaseOrProxy)
                .map(TransactionPhase.class::cast)
                .collect(Collectors.toMap(
                        TransactionPhase::getEventClass,
                        obj -> obj
                ));
    }

    private static boolean isTransactionPhaseOrProxy(Object obj) {
        if (obj instanceof TransactionPhase) return true;
        if (AopUtils.isAopProxy(obj)) {
            for (Class<?> interfaceClass : AopProxyUtils.proxiedUserInterfaces(obj)) {
                if(interfaceClass.equals(TransactionPhase.class)) return true;
            }
        }
        return false;
    }

}
