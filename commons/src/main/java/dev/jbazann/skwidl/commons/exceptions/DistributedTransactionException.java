package dev.jbazann.skwidl.commons.exceptions;

public class DistributedTransactionException extends RuntimeException {
    public DistributedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
