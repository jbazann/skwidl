package dev.jbazann.skwidl.commons.exceptions;

// TODO add comment specifying when to use this, refactor usages.
public class UnexpectedResponseException extends RuntimeException {
    public UnexpectedResponseException() {
        super();
    }
    public UnexpectedResponseException(String message) {
        super(message);
    }
}
