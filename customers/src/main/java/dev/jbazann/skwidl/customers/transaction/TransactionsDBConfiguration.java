package dev.jbazann.skwidl.customers.transaction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "dev.jbazann.skwidl.customers.transaction",
        entityManagerFactoryRef = "transactionEntityManagerFactory",
        transactionManagerRef = "transactionTransactionManager"
)
public class TransactionsDBConfiguration {

    @Bean("transactionDataSource")
    @ConfigurationProperties("spring.datasource.transaction")
    public DataSource transactionDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("transactionEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean transactionEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("transactionDataSource") DataSource transactionDataSource) {
        return builder
                .dataSource(transactionDataSource)
                .packages("dev.jbazann.skwidl.customers.transaction")
                .persistenceUnit("transaction")
                .build();
    }

    @Bean("transactionTransactionManager")
    public PlatformTransactionManager transactionTransactionManager(
            @Qualifier("transactionEntityManagerFactory") LocalContainerEntityManagerFactoryBean transactionEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(transactionEntityManagerFactory.getObject()));
    }

}
