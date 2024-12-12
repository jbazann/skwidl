package com.jbazann.orders.commons.async.transactions;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class TransactionsConfiguration {

    @Bean
    public TransactionPhaseExecutor transactionExecutorService(@Lazy TransactionPhaseRegistrar transactionPhaseRegistrarService) {
        return new TransactionPhaseExecutor(transactionPhaseRegistrarService);
    }

    @Lazy
    @Bean
    public TransactionPhaseRegistrar standardTransactionRegistrarService(ApplicationContext applicationContext) {
        return new TransactionPhaseRegistrar(applicationContext);
    }


}
