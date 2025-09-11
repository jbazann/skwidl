package dev.jbazann.skwidl.commons.aspect.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
        logger.info("Invoking " + joinPoint.getSignature().toShortString() +
                        (joinPoint.getArgs().length > 0 ?
                                " with argument(s):\n" + Arrays.toString(joinPoint.getArgs()) :
                                "")
        );
    }

    @Around("dev.jbazann.skwidl.commons.aspect.BasicPointcuts.repositoryExecutions()")
    public Object logRepositoryExecutions(ProceedingJoinPoint pjp) 
        throws Throwable {
        logger.info("Invoking " + pjp.getSignature().toShortString() +
                    (pjp.getArgs().length > 0 ?
                            " with argument(s):\n" + Arrays.toString(pjp.getArgs()) :
                            "")
        );
        Object result = pjp.proceed(pjp.getArgs());
        logger.info("Repository call returned:\n" + result);
        return result;
    }

}
