package dev.jbazann.skwidl.commons.shared.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jbazann.commons.redis")
public class RedisConfigurationProperties {
    private String host;
    private int port;
}