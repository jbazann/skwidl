package dev.jbazann.skwidl.commons.aspect.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.client.RestClient;

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

    @Around(value = "dev.jbazann.skwidl.commons.aspect.BasicPointcuts.restClientRequest(handler)",
            argNames = "pjp,handler") // pjp included due to IntelliJ's linting. May be omitted.
    public Object logRestClientRequests(ProceedingJoinPoint pjp,
                                      RestClient.RequestHeadersSpec.ExchangeFunction<?> handler)
            throws Throwable {
        Object[] args = pjp.getArgs();
        if (args.length != 1) {
            logger.warning("BasicLoggingAspect#logRestClientRequests() found unexpected argument amount.");
        }
        RestClient.RequestHeadersSpec.ExchangeFunction<?> handlerWrapper = (req, resp) -> {
            logger.info(String.format( // TODO use interceptors or built-in logging to run this as the request is sent.
                    "REQUEST %s %s with attributes %s",
                    req.getMethod(),
                    req.getURI(),
                    req.getAttributes()
            ));
            logger.info(String.format(
                    "RESPONSE %d",
                    resp.getStatusCode().value() // TODO send error strings as headers so they can be logged here
            ));
            return handler.exchange(req, resp);
        };
        args[0] = handlerWrapper;
        return pjp.proceed(args);
    }

}
