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
            "dev.jbazann.skwidl.commons.aspect.BasicPointcuts.controllerExecutions() || " +
            "dev.jbazann.skwidl.commons.aspect.BasicPointcuts.repositoryExecutions()")
    public void logMethodExecutions(JoinPoint joinPoint) {
        logger.info("Invoking " + joinPoint.getSignature().toShortString() +
                        (joinPoint.getArgs().length > 0 ?
                                " with argument(s) " + Arrays.toString(joinPoint.getArgs()) :
                                "")
        );
    }

}
