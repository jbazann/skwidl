package dev.jbazann.skwidl.commons.exceptions;

/**
 * For use exclusively within {@link dev.jbazann.skwidl.commons} and
 * subpackages. Indicates an internal error I was too lazy to handle
 * professionally.
 */
public class CommonsInternalException extends RuntimeException {
    public CommonsInternalException() {}
    public CommonsInternalException(String message) {
        super(message);
    }
    public CommonsInternalException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommonsInternalException(Throwable cause) {
        super(cause);
    }
}
