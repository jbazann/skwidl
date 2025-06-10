package dev.jbazann.skwidl.commons.config;

import dev.jbazann.skwidl.commons.shared.storage.RedisConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:commons.properties")
@EnableConfigurationProperties({
        RedisConfigurationProperties.class,
        AsyncConfigurationProperties.class
})
public class CommonsConfiguration {
}
