package dev.jbazann.skwidl.commons.advice.api;

import dev.jbazann.skwidl.commons.advice.ExceptionMaskingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ExceptionMaskingConfiguration.class})
public @interface EnableExceptionMasking {
}
