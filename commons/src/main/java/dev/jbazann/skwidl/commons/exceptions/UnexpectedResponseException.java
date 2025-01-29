package dev.jbazann.skwidl.commons.exceptions;

public class UnexpectedResponseException extends RuntimeException {
    public UnexpectedResponseException() {
        super();
    }
    public UnexpectedResponseException(String message) {
        super(message);
    }
}
