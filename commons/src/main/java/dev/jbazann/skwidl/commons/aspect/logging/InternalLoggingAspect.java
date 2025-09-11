package dev.jbazann.skwidl.commons.aspect.logging;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
public class InternalLoggingAspect {

    private final Logger logger;

    public InternalLoggingAspect(Logger logger) {
        this.logger = logger;
    }

    @PostConstruct
    public void init() {
        logger.info(String.format(
                "Starting %s bean.",
                getClass().getSimpleName()
        ));
    }

    @Before(value = "dev.jbazann.skwidl.commons.aspect.InternalPointcuts.beanInstantiations(bean)",
            argNames = "joinPoint,bean")  // joinPoint included due to IntelliJ's linting. May be omitted.
    public void logBeanInstantiations(JoinPoint joinPoint, Bean bean) { // TODO you know something needs to be done here....
        logger.info(String.format(
                "Creating %s bean as %s%s",
                ((MethodSignature) joinPoint.getSignature()).getReturnType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(bean.name()) // 🤮
        ));
    }

    @Before(value = "dev.jbazann.skwidl.commons.aspect.InternalPointcuts.rabbitDomainEventListenerMethod(listener,event)",
            argNames = "joinPoint,listener,event") // joinPoint included due to IntelliJ's linting. May be omitted.
    public void logRabbitListenerExecutions(
            JoinPoint joinPoint,
            RabbitListener listener,
            DomainEvent event
    ) {
        logger.info(String.format(
                "Calling RabbitListener \"%s\" in \"%s\" with event:\n%s.",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName(),
                event.toString()
        ));
    }

    @Before("dev.jbazann.skwidl.commons.aspect.InternalPointcuts.rabbitPublishMethod()")
    public void logRabbitPublishExecutions(
        JoinPoint joinPoint
    ) {
        logger.info(String.format(
                "Calling RabbitPublisher#%s with args:\n %s.",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        ));
    }

}
