package dev.jbazann.skwidl.commons.exceptions;

public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException() {
        super();
    }
    public UnknownErrorException(String message) {
        super(message);
    }
}
