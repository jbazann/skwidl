package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionRepository;
import dev.jbazann.skwidl.commons.shared.storage.RedisConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@Import(RedisConfiguration.class)
public class TransactionsConfiguration {

    @Bean
    public TransactionLifecycleActions transactionLifecycleActions(TransactionRepository repository) {
        return new TransactionLifecycleActions(repository);
    }

    @Bean
    TransactionLockingService transactionLockingService(RedissonClient client) {
        return new TransactionLockingService(client);
    }

    @Bean
    public TransactionStageExecutorService transactionExecutorService(@Lazy TransactionStageRegistrarService registrar,
                                                                      TransactionLifecycleActions actions,
                                                                      TransactionLockingService lockingService) {
        return new TransactionStageExecutorService(registrar, actions, lockingService);
    }

    @Lazy
    @Bean
    public TransactionStageRegistrarService standardTransactionRegistrarService(ApplicationContext applicationContext) {
        return new TransactionStageRegistrarService(applicationContext);
    }


}
