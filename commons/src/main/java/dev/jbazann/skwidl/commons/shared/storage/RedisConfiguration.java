package dev.jbazann.skwidl.commons.shared.storage;

import dev.jbazann.skwidl.commons.config.CommonsConfiguration;
import dev.jbazann.skwidl.commons.shared.storage.converters.ApplicationMemberToBytesConverter;
import dev.jbazann.skwidl.commons.shared.storage.converters.BytesToApplicationMemberConverter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.List;

@Configuration
@EnableRedisRepositories(basePackages = {
        "dev.jbazann.skwidl.commons.async.transactions.entities"
})
@Import(CommonsConfiguration.class)
public class RedisConfiguration {
// TODO redis doesn't actually fit the use case, replace it

    @Bean
    public ApplicationMemberToBytesConverter applicationMemberToBytesConverter() {
        return new ApplicationMemberToBytesConverter();
    }

    @Bean BytesToApplicationMemberConverter bytesToApplicationMemberConverter() {
        return new BytesToApplicationMemberConverter();
    }

    @Bean
    public RedisCustomConversions redisCustomConversions(
            ApplicationMemberToBytesConverter amtb,
            BytesToApplicationMemberConverter btam
    ) {
        return new RedisCustomConversions(List.of(amtb, btam));
    }


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
