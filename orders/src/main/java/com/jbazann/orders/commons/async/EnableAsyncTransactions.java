package com.jbazann.orders.commons.async;

import com.jbazann.orders.commons.async.orchestration.OrchestrationConfiguration;
import com.jbazann.orders.commons.async.rabbitmq.RabbitConfiguration;
import com.jbazann.orders.commons.async.transactions.TransactionsConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
        RabbitConfiguration.class,
        TransactionsConfiguration.class,
        OrchestrationConfiguration.class,
})
public @interface EnableAsyncTransactions {



}
