package dev.jbazann.skwidl.commons.rest;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("loggingRestClientBuilder")
    public RestClient.Builder loggingRestClientBuilder(RestClientBuilderConfigurer c) {
        RestClient.Builder clientBuilder = RestClient.builder();
        clientBuilder.requestInterceptor(new LoggingRequestInterceptor());
        return clientBuilder;
    }

}
