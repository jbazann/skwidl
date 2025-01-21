package dev.jbazann.skwidl.commons.shared.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@PropertySource("classpath:commons.properties")
@EnableConfigurationProperties(RedisConfigurationProperties.class)
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisConfigurationProperties properties) {
        System.out.printf("### SKWIDL ### REDIS HOST: %s ### REDIS PORT: %d ###\n", properties.getHost(), properties.getPort());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
                properties.getHost(), properties.getPort());
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
