package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.stream.Collectors;

@Validated
public class TransactionStageRegistrarService {

    public record StageKey(Class<? extends DomainEvent> eventClass, DomainEvent.Type eventType) {}

    private final Map<StageKey, TransactionStage> stages;

    //TODO check instantiation timing (must occur when stages exist)
    //TODO static factory method
    public TransactionStageRegistrarService(ApplicationContext applicationContext) {
        stages = mapStages(applicationContext.getBeansWithAnnotation(TransactionStageBean.class));
    }

    public @NotNull @Valid TransactionStage getStageForEvent(@NotNull @Valid DomainEvent event) {
        StageKey key = new StageKey(event.getClass(), event.type());
        if (!stages.containsKey(key)) throw new IllegalStateException(String.format(
                "No TransactionStage registered for event ID %s of type %s.", event.id(), event.type()));
        return stages.get(key);
    }

    private static Map<StageKey, TransactionStage> mapStages(Map<String, Object> beans) {
        return beans.values().stream()
                .filter(TransactionStageRegistrarService::isTransactionStageOrProxy)
                .map(TransactionStage.class::cast)
                .collect(Collectors.toMap(
                        stage -> new StageKey(getAnnotatedEventClass(stage),getAnnotatedStage(stage).trigger),
                        stage -> stage
                ));
    }

    private static boolean isTransactionStageOrProxy(Object obj) {
        if (obj instanceof TransactionStage) return true;
        if (AopUtils.isAopProxy(obj)) {
            for (Class<?> interfaceClass : AopProxyUtils.proxiedUserInterfaces(obj)) {
                if(interfaceClass.equals(TransactionStage.class)) return true;
            }
        }
        return false;
    }

    private static Stage getAnnotatedStage(TransactionStage stage) {
        Stage type = getAnnotation(stage).stage();
        if (type == null) throw new IllegalStateException("TransactionStage beans must " +
                "define TransactionStageBean::stage.");
        return type;
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
