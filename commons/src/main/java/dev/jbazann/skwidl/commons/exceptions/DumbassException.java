package dev.jbazann.skwidl.commons.exceptions;

/**
 * To be used when checking an unlikely edge case that
 * could only be caused by doing things the wrong
 * way on purpose and refusing to take them seriously.
 */
public class DumbassException extends RuntimeException {
    public DumbassException() {
        super();
    }

    public DumbassException(String message) {
        super(message);
    }

    public DumbassException(String message, Throwable cause) {
        super(message, cause);
    }

    public DumbassException(Throwable cause) {
        super(cause);
    }
}
