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


@Configuration
@PropertySource("classpath:commons.properties")
@EnableConfigurationProperties(RedisConfigurationProperties.class)
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisConfigurationProperties properties) {
        final String RedisAddress = String.format("redis://%s:%d", properties.getHost(), properties.getPort());
        Config config = new Config();
        config.useSingleServer().setAddress(RedisAddress);
        RedissonClient client = Redisson.create(config);
        return  new RedissonConnectionFactory(client);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
