package dev.jbazann.skwidl.orders;

import dev.jbazann.skwidl.commons.advice.api.EnableExceptionMasking;
import dev.jbazann.skwidl.commons.aspect.api.EnableLogging;
import dev.jbazann.skwidl.commons.async.api.DistributedTransactionCoordinator;
import dev.jbazann.skwidl.commons.async.api.DistributedTransactionMember;
import dev.jbazann.skwidl.commons.async.api.DistributedTransactionStarter;
import dev.jbazann.skwidl.commons.rest.api.IncludeRestPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication()
@EnableMongoRepositories("dev.jbazann.skwidl.orders.order")
@EnableLogging // should appear before any other project annotation to configure logging as early as possible
@EnableExceptionMasking
// @EnableTransactionManagement
@DistributedTransactionCoordinator
@DistributedTransactionMember
@DistributedTransactionStarter
@IncludeRestPackage
//TODO review local transactions, locking
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}

	@Bean
	public HttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}

	// @Bean
    // public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    //     return new MongoTransactionManager(dbFactory);
    // }

}
