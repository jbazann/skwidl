package dev.jbazann.skwidl.commons.async.rabbitmq;

import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.logging.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter plainJackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("RabbitTemplateMessageConverterCustomizer")
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(
            @Qualifier("plainJackson2JsonMessageConverter")
            MessageConverter converter
    ) {
        return template -> template.setMessageConverter(converter);
    }

    @Bean
    public RabbitPublisher rabbitPublisher(
            DomainEventBuilderFactory factory,
            RabbitMessagingTemplate rabbitMessagingTemplate,
            @Value("${jbazann.rabbit.exchanges.event}") String eventExchange
    ) {
        RabbitPublisher publisher = new RabbitPublisher(factory,rabbitMessagingTemplate,eventExchange);
        LoggerFactory.get(getClass()).debug(
                "CREATING RabbitPublisher with MessageConverter class: {} — {}.",
                rabbitMessagingTemplate.getRabbitTemplate().getMessageConverter().getClass().getSimpleName(),
                publisher
        );
        return publisher;
    }

}
