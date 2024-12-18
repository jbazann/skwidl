package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.transactions.data.TransactionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class TransactionsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TransactionRepository missingTransactionRepository() {
        throw new IllegalStateException("A TransactionRepository @Bean was not provided.");
    }

    @Bean
    public TransactionPhaseExecutor transactionExecutorService(@Lazy TransactionPhaseRegistrar registrar, TransactionRepository repository) {
        return new TransactionPhaseExecutor(registrar, repository);
    }

    @Lazy
    @Bean
    public TransactionPhaseRegistrar standardTransactionRegistrarService(ApplicationContext applicationContext) {
        return new TransactionPhaseRegistrar(applicationContext);
    }


}
