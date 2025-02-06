package dev.jbazann.skwidl.commons.aspect.api;

import dev.jbazann.skwidl.commons.aspect.logging.LoggingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(LoggingConfiguration.class)
public @interface EnableLogging {
}
