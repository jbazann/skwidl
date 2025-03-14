package dev.jbazann.skwidl.commons.aspect.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class LoggingConfiguration {

    @Bean
    public static InternalLoggingAspect internalLoggingAspect() {
        return new InternalLoggingAspect(Logger.getLogger(InternalLoggingAspect.class.getName()));
    }

    @Bean
    public BasicLoggingAspect basicLoggingAspect() {
        return new BasicLoggingAspect(Logger.getLogger(BasicLoggingAspect.class.getName()));
    }

}
