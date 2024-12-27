package com.jbazann.commons.async.api;

import com.jbazann.commons.async.orchestration.OrchestrationCoordinatorConfiguration;
import com.jbazann.commons.async.transactions.TransactionsConfiguration;
import com.jbazann.commons.identity.IdentityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        TransactionsConfiguration.class,
        OrchestrationCoordinatorConfiguration.class,
        IdentityConfiguration.class,
})
public @interface DistributedTransactionCoordinator {
}
