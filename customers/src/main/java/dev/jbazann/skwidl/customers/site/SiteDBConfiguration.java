package dev.jbazann.skwidl.customers.site;

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
        basePackages = "dev.jbazann.skwidl.customers.site",
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
            EntityManagerFactoryBuilder builder, @Qualifier("siteDataSource") DataSource siteDataSource) {
        return builder
                .dataSource(siteDataSource)
                .packages("dev.jbazann.skwidl.customers.site")
                .persistenceUnit("site")
                .build();
    }

    @Bean
    public PlatformTransactionManager siteTransactionManager(
            @Qualifier("siteEntityManagerFactory") LocalContainerEntityManagerFactoryBean siteEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(siteEntityManagerFactory.getObject()));
    }
}
