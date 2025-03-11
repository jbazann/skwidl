package dev.jbazann.skwidl.commons.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class InternalPointcuts {

    @Pointcut(value = "beanAnnotatedMethod(bean)", argNames = "bean")
    public void beanInstantiations(org.springframework.context.annotation.Bean bean) {}

    @Pointcut("@annotation(bean) && execution(* dev.jbazann.skwidl..*.*(..))")
    private void beanAnnotatedMethod(org.springframework.context.annotation.Bean bean) {}

    @Pointcut("@target(org.springframework.context.annotation.Configuration)")
    private void inConfigurationClass() {}

}
