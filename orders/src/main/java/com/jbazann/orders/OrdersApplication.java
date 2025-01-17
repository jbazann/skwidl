package com.jbazann.orders;

import com.jbazann.commons.async.api.DistributedTransactionCoordinator;
import com.jbazann.commons.async.api.DistributedTransactionMember;
import com.jbazann.commons.async.api.DistributedTransactionStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication()
@EnableMongoRepositories("com.jbazann.orders.order")
@EnableTransactionManagement
@DistributedTransactionCoordinator
@DistributedTransactionMember
@DistributedTransactionStarter
//TODO review local transactions, locking
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}

	@Bean
	public HttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}

}
