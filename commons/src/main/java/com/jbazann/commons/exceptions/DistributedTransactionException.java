package com.jbazann.commons.exceptions;

public class DistributedTransactionException extends RuntimeException {
    public DistributedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
