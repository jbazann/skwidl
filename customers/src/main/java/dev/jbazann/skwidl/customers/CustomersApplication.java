package dev.jbazann.skwidl.customers;

import dev.jbazann.skwidl.commons.aspect.api.EnableLogging;
import dev.jbazann.skwidl.commons.async.api.DistributedTransactionMember;
import dev.jbazann.skwidl.commons.rest.api.IncludeRestPackage;
import dev.jbazann.skwidl.customers.customer.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@EnableLogging
@EnableAsync
@DistributedTransactionMember
@IncludeRestPackage
public class CustomersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomersApplication.class, args);
	}

	@Bean
	public HttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}

	@Bean
	public Customer.DefaultValues customerDefaults() {
		return new Customer.DefaultValues();
	}

}
