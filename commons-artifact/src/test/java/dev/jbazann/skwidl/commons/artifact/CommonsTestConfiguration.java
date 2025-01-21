package dev.jbazann.skwidl.commons.artifact;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class CommonsTestConfiguration {

    @Bean // Prevents unknown host Exceptions caused by WebApplicationContext attempting to connect before the container exists.
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.0.3-alpine"));
    }

    @Bean
    public WebTestClient webTestClient(WebApplicationContext wac) {
        return MockMvcWebTestClient.bindToApplicationContext(wac).build();
    }

}
