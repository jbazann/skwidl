package dev.jbazann.skwidl.orders.order.exceptions;

/**
 * To be thrown by controllers when they have enough information to
 * determine a status 400 response.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
