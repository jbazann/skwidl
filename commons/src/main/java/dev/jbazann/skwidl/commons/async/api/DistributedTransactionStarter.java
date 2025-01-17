package dev.jbazann.skwidl.commons.async.api;

import dev.jbazann.skwidl.commons.async.events.EventsConfiguration;
import dev.jbazann.skwidl.commons.async.transactions.TransactionsConfiguration;
import dev.jbazann.skwidl.commons.identity.IdentityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        TransactionsConfiguration.class,
        EventsConfiguration.class,
        IdentityConfiguration.class
})
public @interface DistributedTransactionStarter {
}
