package dev.jbazann.skwidl.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jbazann.commons.async")
public class AsyncConfigurationProperties {
    private String defaultCoordinator;
    private String identity;
}
