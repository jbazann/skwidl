package dev.jbazann.skwidl.commons.advice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionMaskingConfiguration {

    @Bean
    public ExceptionMaskingAdvice exceptionMaskingAdvice() {
        return new ExceptionMaskingAdvice();
    }

}
