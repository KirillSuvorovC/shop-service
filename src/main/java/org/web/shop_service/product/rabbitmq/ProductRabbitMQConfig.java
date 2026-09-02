package org.web.shop_service.product.rabbitmq;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRabbitMQConfig {
    public static final String EXCHANGE = "productExchange";
    @Bean
    @Qualifier(EXCHANGE)
    public TopicExchange exchange(ProductRabbitMQProperties properties) {
        return ExchangeBuilder
                .topicExchange(properties.getExchange())
                .durable(true)
                .build();
    }
}
