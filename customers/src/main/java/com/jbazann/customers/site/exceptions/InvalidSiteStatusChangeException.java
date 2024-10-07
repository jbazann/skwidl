package com.jbazann.customers.site.exceptions;

public class InvalidSiteStatusChangeException extends RuntimeException {
    public InvalidSiteStatusChangeException(String message) {
        super(message);
    }
}
