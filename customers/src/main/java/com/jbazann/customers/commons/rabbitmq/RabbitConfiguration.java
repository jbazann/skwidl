package com.jbazann.customers.commons.rabbitmq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitConfiguration {

    /**
     * If this bean is unique it will be injected into
     * {@link org.springframework.amqp.rabbit.core.RabbitTemplate}
     * wrapped by the autoconfigured {@link org.springframework.amqp.rabbit.core.RabbitMessagingTemplate}
     * bean.
     */
    @Bean
    @Primary
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
