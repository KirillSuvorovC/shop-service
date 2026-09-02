package org.web.shop_service.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DeadLetterRabbitMQProperties {
    @Value("${app.rabbitmq.dead-letter.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.dead-letter.routing-key-error}")
    private String routingKeyError;
    @Value("${app.rabbitmq.dead-letter.queue}")
    private String queue;
}