package dev.jbazann.skwidl.commons.aspect;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import org.aspectj.lang.annotation.Pointcut;

public class InternalPointcuts {

    @Pointcut(value = "beanAnnotatedMethod(bean)", argNames = "bean")
    public void beanInstantiations(org.springframework.context.annotation.Bean bean) {}

    @Pointcut("@annotation(listener) && args(event) && " +
            "execution(* dev.jbazann.skwidl..*.*(dev.jbazann.skwidl.commons.async.events.DomainEvent))")
    public void rabbitDomainEventListenerMethod(
            org.springframework.amqp.rabbit.annotation.RabbitListener listener,
            DomainEvent event
    ) {}

    @Pointcut("@annotation(bean) && execution(* dev.jbazann.skwidl..*.*(..))")
    private void beanAnnotatedMethod(org.springframework.context.annotation.Bean bean) {}

    @Pointcut("@target(org.springframework.context.annotation.Configuration)")
    private void inConfigurationClass() {}

}
