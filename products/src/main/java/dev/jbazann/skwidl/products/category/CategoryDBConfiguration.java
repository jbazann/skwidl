package dev.jbazann.skwidl.products.category;

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
        basePackages = "dev.jbazann.skwidl.products.category",
        entityManagerFactoryRef = "categoryEntityManagerFactory",
        transactionManagerRef = "categoryTransactionManager"
)
public class CategoryDBConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.category")
    public DataSource categoryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean categoryEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("categoryDataSource") DataSource categoryDataSource) {
        return builder
                .dataSource(categoryDataSource)
                .packages("dev.jbazann.skwidl.products.category")
                .persistenceUnit("category")
                .build();
    }

    @Bean
    public PlatformTransactionManager categoryTransactionManager(
            @Qualifier("categoryEntityManagerFactory") LocalContainerEntityManagerFactoryBean categoryEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(categoryEntityManagerFactory.getObject()));
    }

}
