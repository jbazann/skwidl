package dev.jbazann.skwidl.commons.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class BasicPointcuts {

    @Pointcut("anyMethod() && inProjectPackages() && inAnnotatedService()")
    public void serviceExecutions() {}

    @Pointcut("anyMethod() && inProjectPackages() && inAnnotatedController()")
    public void controllerExecutions() {}

    @Pointcut("projectRepositoryMethod()")
    public void repositoryExecutions() {}

    @Pointcut("execution(* *(..))")
    private void anyMethod() {}

    @Pointcut("execution(* dev.jbazann.skwidl..*.*Repository.*(..))")
    private void projectRepositoryMethod() {}

    @Pointcut("within(dev.jbazann.skwidl..*)")
    private void inProjectPackages() {}

    @Pointcut("@target(org.springframework.stereotype.Service)")
    private void inAnnotatedService() {}

    @Pointcut("@target(org.springframework.stereotype.Controller) || @target(org.springframework.web.bind.annotation.RestController)")
    private void inAnnotatedController() {}

}
