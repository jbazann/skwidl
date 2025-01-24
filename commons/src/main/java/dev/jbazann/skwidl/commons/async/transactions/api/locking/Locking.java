package dev.jbazann.skwidl.commons.async.transactions.api.locking;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Locking {

    @AliasFor("strategy")
    LockingStrategies value() default LockingStrategies.EPHEMERAL;

    @AliasFor("value")
    LockingStrategies strategy() default LockingStrategies.EPHEMERAL;

    LockingActions action() default LockingActions.GET;

    enum LockingActions {
        GET, RELEASE;
    }

}
