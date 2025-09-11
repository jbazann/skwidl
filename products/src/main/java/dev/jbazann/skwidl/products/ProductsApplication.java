package dev.jbazann.skwidl.products;

import dev.jbazann.skwidl.commons.aspect.api.EnableLogging;
import dev.jbazann.skwidl.commons.async.api.DistributedTransactionMember;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.rest.api.IncludeRestPackage;
import dev.jbazann.skwidl.products.transaction.TransactionEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.function.Supplier;

@SpringBootApplication
@EnableLogging
@Import(
		ProductsApplication.EarlyConfiguration.class
)
@DistributedTransactionMember
@IncludeRestPackage
public class ProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

	@Bean
	public HttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}

	@Configuration
	public static class EarlyConfiguration {

		@Bean
		public Supplier<Transaction> transactionSupplier() {
			return TransactionEntity::new;
		}

	}

}
