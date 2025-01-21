package dev.jbazann.skwidl.commons.shared.storage;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@Configuration
@PropertySource("classpath:commons.properties")
@EnableConfigurationProperties(RedisConfigurationProperties.class)
@EnableRedisRepositories(basePackages = {
        "dev.jbazann.skwidl.commons.async.transactions.entities"
})
public class RedisConfiguration {


    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisConfigurationProperties properties) {
        final String RedisAddress = String.format("redis://%s:%d", properties.getHost(), properties.getPort());
        Config config = new Config();
        config.useSingleServer().setAddress(RedisAddress);
        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedissonClient client) {
        return  new RedissonConnectionFactory(client);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
