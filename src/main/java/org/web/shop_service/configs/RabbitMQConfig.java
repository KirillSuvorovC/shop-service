package org.web.shop_service.configs;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String ERROR_ROUTING_KEY = "error";
    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    public static final String DEAD_LETTER_QUEUE = "errors.dlq";

    @Bean
    @Qualifier(DEAD_LETTER_EXCHANGE)
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    @Qualifier(DEAD_LETTER_QUEUE)
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    @Qualifier(DEAD_LETTER_QUEUE)
    public Binding deadLetterBinding(@Qualifier(DEAD_LETTER_QUEUE) Queue queue,
                                     @Qualifier(RabbitMQConfig.DEAD_LETTER_EXCHANGE)
                                     DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ERROR_ROUTING_KEY);
    }
}
