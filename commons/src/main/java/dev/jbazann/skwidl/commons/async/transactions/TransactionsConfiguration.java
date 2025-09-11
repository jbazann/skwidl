package dev.jbazann.skwidl.commons.async.transactions;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionRepository;
import dev.jbazann.skwidl.commons.logging.Logger;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import dev.jbazann.skwidl.commons.shared.storage.RedisConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.util.function.Supplier;

@Configuration
@Import({
        RedisConfiguration.class,
})
public class TransactionsConfiguration {

    private static final Logger log = LoggerFactory.get(TransactionsConfiguration.class);

    @Bean
    public Module transactionModule(Supplier<Transaction> transactionSupplier) {
        return new SimpleModule()
                .addAbstractTypeMapping(Transaction.class, transactionSupplier.get().getClass());
    }

    @Bean
    @ConditionalOnMissingBean
    public Supplier<Transaction> transactionSupplier() {
        throw new IllegalStateException("Must provide a concrete Transaction supplier bean \"transactionSupplier\".");
    }

    @Bean
    public TransactionLifecycleActions transactionLifecycleActions(
            TransactionRepository repository,
            @Qualifier("transactionSupplier") Supplier<Transaction> supplier
    ) {
        return new TransactionLifecycleActions(repository, supplier);
    }

    @Bean
    public TransactionLockingService transactionLockingService(RedissonClient client) {
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
