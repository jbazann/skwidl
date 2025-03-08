package dev.jbazann.skwidl.commons.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.client.RestClient;

@Aspect
public class BasicPointcuts {

    @Pointcut("anyMethod() && inProjectPackages() && inAnnotatedService()")
    public void serviceExecutions() {}

    @Pointcut("anyMethod() && inProjectPackages() && inAnnotatedController()")
    public void controllerExecutions() {}

    @Pointcut(value = "restClientExchange(handler) && inProjectPackages()", argNames = "handler")
    public void restClientRequest(RestClient.RequestHeadersSpec.ExchangeFunction<?> handler) {}

    @Pointcut("execution(* *(..))")
    private void anyMethod() {}

    @Pointcut("within(dev.jbazann.skwidl..*)")
    private void inProjectPackages() {}

    @Pointcut("@target(org.springframework.stereotype.Service)")
    private void inAnnotatedService() {}

    @Pointcut("@target(org.springframework.stereotype.Controller)")
    private void inAnnotatedController() {}

    @Pointcut("execution(* org.springframework.web.client.RestClient.RequestBodySpec.exchange(..)) && args(handler)")
    private void restClientExchange(RestClient.RequestHeadersSpec.ExchangeFunction<?> handler) {}

}
