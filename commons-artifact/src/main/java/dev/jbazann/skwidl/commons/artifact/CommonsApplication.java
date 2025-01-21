package dev.jbazann.skwidl.commons.artifact;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitConfiguration;
import dev.jbazann.skwidl.commons.shared.storage.SharedStorageClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SharedStorageClient
@SpringBootApplication(exclude = {
        RedisAutoConfiguration.class,
        RabbitAutoConfiguration.class
})
public class CommonsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonsApplication.class, args);
    }

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

}