package com.jbazann.orders.commons.async.rabbitmq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableRabbit
@Configuration
public class RabbitConfiguration {

    /**
     * If this bean is unique, it will be injected into the
     * {@link org.springframework.amqp.rabbit.core.RabbitTemplate}
     * instance wrapped by the autoconfigured {@link org.springframework.amqp.rabbit.core.RabbitMessagingTemplate}
     * bean, which is then used by {@link RabbitPublisher}.
     */
    @Bean
    @Primary
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
