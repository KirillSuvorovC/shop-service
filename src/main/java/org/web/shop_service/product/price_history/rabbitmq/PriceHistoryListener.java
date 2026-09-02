package org.web.shop_service.product.price_history.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web.shop_service.product.events.ProductPriceChangedEvent;
import org.web.shop_service.product.price_history.ProductPriceHistoryService;

@Component
@RequiredArgsConstructor
public class PriceHistoryListener {
    private final ProductPriceHistoryService service;
    @RabbitListener(queues = "#{priceHistoryRabbitMQProperties.queuePriceChanged}")
    @Transactional
    public void handle(ProductPriceChangedEvent event) {
        service.saveChange(event);
    }
}
