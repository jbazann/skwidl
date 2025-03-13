package dev.jbazann.skwidl.commons.rest.api;

import dev.jbazann.skwidl.commons.rest.RestClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RestClientConfiguration.class})
public @interface IncludeRestPackage {
}
