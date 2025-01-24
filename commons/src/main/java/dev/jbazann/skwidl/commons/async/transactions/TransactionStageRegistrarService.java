package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.*;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TransactionStageRegistrarService {

    public record StageKey(Class<? extends DomainEvent> eventClass, DomainEvent.Type eventType) {}

    private final Map<StageKey, TransactionStage> stages;

    //TODO check instantiation timing (must occur when stages exist)
    //TODO static factory method
    public TransactionStageRegistrarService(ApplicationContext applicationContext) {
        stages = mapStages(applicationContext.getBeansWithAnnotation(TransactionStageBean.class));
    }

    public Optional<TransactionStage> getStageForEvent(DomainEvent event) {
        StageKey key = new StageKey(event.getClass(), event.type());
        if (!stages.containsKey(key)) return Optional.empty();
        return Optional.of(stages.get(key));
    }

    private static Map<StageKey, TransactionStage> mapStages(Map<String, Object> beans) {
        return beans.values().stream()
                .filter(TransactionStageRegistrarService::isTransactionPhaseOrProxy)
                .map(TransactionStage.class::cast)
                .collect(Collectors.toMap(
                        stage -> new StageKey(getAnnotatedEventClass(stage),getAnnotatedEventType(stage)),
                        stage -> stage
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

    private static DomainEvent.Type getAnnotatedEventType(TransactionStage stage) {
        DomainEvent.Type type = getAnnotation(stage).eventType();
        if (type == null) throw new IllegalStateException("TransactionStage beans must " +
                "define TransactionStageBean::eventType.");
        return type;
    }

    private static Stage getAnnotatedStage(TransactionStage stage) {
        Stage _stage = getAnnotation(stage).stage();
        if (_stage == null) throw new IllegalStateException("TransactionStage beans must " +
                "define TransactionStageBean::stage.");
        return _stage;
    }

    private static Class<? extends DomainEvent> getAnnotatedEventClass(TransactionStage stage) {
        Class<? extends DomainEvent> eventClass = getAnnotation(stage).eventClass();
        if (eventClass == null) throw new IllegalStateException("TransactionStage beans must " +
                "define TransactionStageBean::eventClass.");
        return eventClass;
    }

    private static TransactionStageBean getAnnotation(TransactionStage stage) {
        if (stage.getClass().isAnnotationPresent(TransactionStageBean.class)) { // TODO yap about defensive programming
            return stage.getClass().getAnnotation(TransactionStageBean.class);
        }
        throw new IllegalStateException("A TransactionStage bean was somehow found without @TransactionStageBean.");
    }

}
