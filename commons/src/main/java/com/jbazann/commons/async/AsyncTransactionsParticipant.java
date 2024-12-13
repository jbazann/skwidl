package com.jbazann.commons.async;

import com.jbazann.commons.async.orchestration.OrchestrationMember;
import com.jbazann.commons.async.transactions.EnableTransactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableTransactions
@OrchestrationMember
public @interface AsyncTransactionsParticipant {



}
