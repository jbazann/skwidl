package dev.jbazann.skwidl.commons.advice;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
