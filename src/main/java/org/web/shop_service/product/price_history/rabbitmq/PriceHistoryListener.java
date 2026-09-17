package org.web.shop_service.product.price_history.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web.shop_service.outbox.publishbox.PublishBoxService;
import org.web.shop_service.product.events.ProductPriceChangedEvent;
import org.web.shop_service.product.price_history.ProductPriceHistoryService;

@Component
@RequiredArgsConstructor
public class PriceHistoryListener {
    public static final String CONSUMER_IDENTIFIER = PriceHistoryListener.class.getName();
    private final ProductPriceHistoryService priceHistoryService;
    private final PublishBoxService publishBoxService;
    @RabbitListener(queues = "#{priceHistoryRabbitMQProperties.queuePriceChanged}")
    @Transactional
    public void handle(ProductPriceChangedEvent event) {
        if(publishBoxService.existsEvent(event.getEventId(), CONSUMER_IDENTIFIER))
            return;
        priceHistoryService.saveChange(event);
        publishBoxService.publishEvent(event, CONSUMER_IDENTIFIER);
    }
}
