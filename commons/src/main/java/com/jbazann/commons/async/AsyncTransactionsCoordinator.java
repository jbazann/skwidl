package com.jbazann.commons.async;

import com.jbazann.commons.async.orchestration.OrchestrationCoordinator;
import com.jbazann.commons.async.transactions.EnableTransactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableTransactions
@OrchestrationCoordinator
public @interface AsyncTransactionsCoordinator {
}
