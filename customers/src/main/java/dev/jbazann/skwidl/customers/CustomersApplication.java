package dev.jbazann.skwidl.customers;

import dev.jbazann.skwidl.commons.aspect.api.EnableLogging;
import dev.jbazann.skwidl.commons.async.api.DistributedTransactionMember;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.rest.api.IncludeRestPackage;
import dev.jbazann.skwidl.customers.customer.Customer;
import dev.jbazann.skwidl.customers.transaction.TransactionEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.function.Supplier;

@SpringBootApplication()
@EnableLogging
@Import(
		CustomersApplication.EarlyConfiguration.class
)
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

	@Configuration
	public static class EarlyConfiguration {

		@Bean
		public Supplier<Transaction> transactionSupplier() {
			return TransactionEntity::new;
		}

	}
}
