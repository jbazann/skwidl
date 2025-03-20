package dev.jbazann.skwidl.customers.customer.exceptions;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Accessors(fluent = true)
@Getter
public class InsufficientCreditException extends RuntimeException {

    private final BigDecimal difference;

    public InsufficientCreditException(String message, BigDecimal difference) {
        super(message);
        this.difference = difference;
    }
}
