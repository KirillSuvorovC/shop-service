package org.web.shop_service.product.price_history.rabbitmq;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PriceHistoryRabbitMQProperties {
    @Value("${app.rabbitmq.price-history.queue-price-changed}")
    private String queuePriceChanged;
}