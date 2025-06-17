package dev.jbazann.skwidl.commons.advice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Profile("controller-advice-is-always-a-component-and-i-do-not-want-it-to-be-accidentally-instantiated")
@RestControllerAdvice
public class ExceptionMaskingAdvice {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException e) {
        UUID id = UUID.randomUUID();
        String message = String.format(
                "CAUGHT: Uncaught exception of type %s, ID: %s, message: %s",
                e.getClass().getName(), id, e.getMessage());
        // TODO use the id and the stack trace to make the logs actually useful
        logger.log(Level.WARNING, message, e);
        return "An unexpected error occurred. Incident ID: " + id;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        UUID id = UUID.randomUUID();
        StringBuilder violations = new StringBuilder("[\n");
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            violations.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getInvalidValue())
                    .append("\nRETURN - ")
                    .append(violation.getExecutableReturnValue())
                    .append("\nPARAMETERS - ")
                    .append(Arrays.toString(violation.getExecutableParameters()))
                    .append("\nLEAF BEAN - ")
                    .append(violation.getLeafBean())
                    .append("\nROOT BEAN - ")
                    .append(violation.getRootBean())
                    .append("\nCONSTRAINT DESCRIPTOR - ")
                    .append(violation.getConstraintDescriptor())
                    .append("\n");
        }
        violations.append("]");
        String message = String.format(
                "CAUGHT: Uncaught exception of type %s, ID: %s, message: %s. \nCONSTRAINT VIOLATIONS: %s",
                e.getClass().getName(), id, e.getMessage(),
                violations
                );
        // TODO use the id and the stack trace to make the logs actually useful
        logger.log(Level.WARNING, message, e);
        return "An unexpected error occurred. Incident ID: " + id;
    }

}
