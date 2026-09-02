package org.web.shop_service.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "deadLetterExchange";
    public static final String QUEUE = "deadLetterQueue";
    private final DeadLetterRabbitMQProperties properties;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Qualifier(EXCHANGE)
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(properties.getExchange());
    }

    @Bean
    @Qualifier(QUEUE)
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(properties.getQueue())
                .build();
    }

    @Bean
    @Qualifier(QUEUE)
    public Binding deadLetterBinding(
            @Qualifier(QUEUE) Queue queue,
            @Qualifier(EXCHANGE) DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.getRoutingKeyError());
    }
}
