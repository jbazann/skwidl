package dev.jbazann.skwidl.gateway;

import dev.jbazann.skwidl.gateway.predicates.PathSegmentMapRoutePredicateFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public HttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}

	@Bean
	public PathSegmentMapRoutePredicateFactory pathSegmentMapRoutePredicateFactory(DiscoveryClient discoveryClient) {
		return new PathSegmentMapRoutePredicateFactory(discoveryClient);
	}

}
