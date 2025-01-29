package dev.jbazann.skwidl.orders.order.exceptions;

public class ReserveFailureException extends RuntimeException {
    public ReserveFailureException() {
        super();
    }
    public ReserveFailureException(String message) {
        super(message);
    }
}
