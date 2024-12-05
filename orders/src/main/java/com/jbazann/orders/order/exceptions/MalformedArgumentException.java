package com.jbazann.orders.order.exceptions;

/**
 * To be thrown by service classes when a parameter value
 * that passed general Jakarta validation constraints and controller-layer checks
 * does not contain valid information.
 * <br> <br>
 * This Exception's use differs from {@link IllegalArgumentException}
 * in that the latter may indicate server errors, while {@link MalformedArgumentException}
 * specifically reports client errors caught beyond the controller layer.
 */
public class MalformedArgumentException extends RuntimeException {
    public MalformedArgumentException(String message) {
        super(message);
    }
}
