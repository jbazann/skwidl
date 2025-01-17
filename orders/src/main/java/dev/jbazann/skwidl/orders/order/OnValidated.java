package dev.jbazann.skwidl.orders.order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For methods that should only be called on valid entities.
 * This annotation will trigger validation before running them.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnValidated {
}
