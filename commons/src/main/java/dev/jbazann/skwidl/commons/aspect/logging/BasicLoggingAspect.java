package dev.jbazann.skwidl.commons.aspect.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
public class BasicLoggingAspect {

    private final Logger logger;

    public BasicLoggingAspect(Logger logger) {
        this.logger = logger;
    }

    @Before("dev.jbazann.skwidl.commons.aspect.BasicPointcuts.serviceExecutions() || " +
            "dev.jbazann.skwidl.commons.aspect.BasicPointcuts.controllerExecutions()")
    public void logMethodExecutions(JoinPoint joinPoint) {
        logger.info(String.format(
                "CALLING %s WITH PARAMETERS %s",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()) // TODO this won't work unless args implement toString but icba right now
        ));
    }

}
