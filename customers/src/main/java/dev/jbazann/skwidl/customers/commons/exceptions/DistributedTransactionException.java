package dev.jbazann.skwidl.customers.commons.exceptions;

public class DistributedTransactionException extends RuntimeException {
    public DistributedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
