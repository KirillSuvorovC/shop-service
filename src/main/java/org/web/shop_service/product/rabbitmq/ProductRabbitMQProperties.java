package org.web.shop_service.product.rabbitmq;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public final class ProductRabbitMQProperties {
    @Value("${app.rabbitmq.product.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.product.routing-key-price-changed}")
    private String routingKeyPriceChanged;
}