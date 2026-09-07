package org.web.shop_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockRabbitMQConfig {
    private static final String PROPERTY = "spring.autoconfigure.exclude";
    private static final Supplier<Object> CONFIG = () ->
            "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration";

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return mock(RabbitTemplate.class);
    }

    @Bean
    public DynamicPropertyRegistrar excludeRabbitMQ() {
        return registry -> registry.add(PROPERTY, CONFIG);
    }
}
