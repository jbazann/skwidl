package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionStageBean {

    @AliasFor(annotation = Component.class)
    String value();

    Class<? extends DomainEvent> eventClass();

    DomainEvent.Type eventType();

    Stage stage();

}
