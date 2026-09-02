package org.web.shop_service.product.price_history.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web.shop_service.configs.DeadLetterRabbitMQProperties;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQConfig;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQProperties;

@RequiredArgsConstructor
@Configuration
public class PriceHistoryRabbitMQConfig {
    public static final String QUEUE_PRICE_CHANGED = "priceHistoryChangedQueue";
    private final DeadLetterRabbitMQProperties deadLetterProperties;
    private final PriceHistoryRabbitMQProperties priceHistoryProperties;
    private final ProductRabbitMQProperties productProperties;
    @Bean
    @Qualifier(QUEUE_PRICE_CHANGED)
    public Queue mainQueue() {
        return QueueBuilder
                .durable(priceHistoryProperties.getQueuePriceChanged())
                .deadLetterExchange(deadLetterProperties.getExchange())
                .deadLetterRoutingKey(deadLetterProperties.getRoutingKeyError())
                .build();
    }

    @Bean
    @Qualifier(QUEUE_PRICE_CHANGED)
    public Binding mainBinding(
            @Qualifier(QUEUE_PRICE_CHANGED) Queue queue,
            @Qualifier(ProductRabbitMQConfig.EXCHANGE) TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(productProperties.getRoutingKeyPriceChanged());
    }
}
