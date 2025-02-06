package dev.jbazann.skwidl.commons.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class BasicPointcuts {

    @Pointcut("anyMethod() && inProjectPackages() && inAnnotatedService()")
    public void serviceExecutions() {}

    @Pointcut("execution(* *(..))")
    private void anyMethod() {}

    @Pointcut("within(dev.jbazann.skwidl..*)")
    private void inProjectPackages() {}

    @Pointcut("@target(org.springframework.stereotype.Service)")
    private void inAnnotatedService() {}

}
