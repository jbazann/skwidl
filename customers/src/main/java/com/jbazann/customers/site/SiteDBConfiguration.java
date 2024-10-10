package com.jbazann.customers.site;

import jakarta.validation.constraints.NotNull;
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
        basePackages = "com.jbazann.customers.site",
        entityManagerFactoryRef = "siteEntityManagerFactory",
        transactionManagerRef = "siteTransactionManager"
)
public class SiteDBConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.site")
    public DataSource siteDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean siteEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("siteDataSource") @NotNull DataSource siteDataSource) {
        return builder
                .dataSource(siteDataSource)
                .packages("com.jbazann.customers.site")
                .persistenceUnit("customerPU")
                .build();
    }

    @Bean
    public PlatformTransactionManager siteTransactionManager(
            @Qualifier("siteEntityManagerFactory") LocalContainerEntityManagerFactoryBean siteEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(siteEntityManagerFactory.getObject()));
    }
}
