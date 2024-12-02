package com.jbazann.orders;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@EnableDiscoveryClient(autoRegister = false) // Current config does not fetch registry, so this effectively disables Discovery to reduce log noise.
public class BasicMockMvcTestConfiguration {

	@Bean // Prevents unknown host Exceptions caused by WebApplicationContext attempting to connect before the container exists.
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
		return new MongoDBContainer(DockerImageName.parse("mongodb/mongodb-community-server:7.0.9-ubuntu2204"));
	}

	@Bean // Prevents unknown host Exceptions caused by WebApplicationContext attempting to connect before the container exists.
	@ServiceConnection
	RabbitMQContainer rabbitContainer() {
		return new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.0.3-alpine"));
	}

	/*
	 * Any and all @ServiceConnection containers required by the WAC should be annotated with @Bean
	 * to prevent "unknown host" Exceptions caused by Spring trying to connect
	 * to their services before replacing the connection properties.
	 */
	@Bean
	public WebTestClient webTestClient(WebApplicationContext wac) {
		return MockMvcWebTestClient.bindToApplicationContext(wac).build();
	}

}
