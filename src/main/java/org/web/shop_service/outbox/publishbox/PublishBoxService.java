package org.web.shop_service.outbox.publishbox;

import org.web.shop_service.outbox.Event;

import java.util.UUID;

public interface PublishBoxService {

    /**
     * Проверяет, было ли уже обработано событие данным потребителем
     * @return {@code true} - если обработано, иначе {@code false}
     */
    boolean existsEvent(UUID outboxEventId, String consumer);

    /**
     * Записывает факт успешной обработки события потребителем
     */
    void publishEvent(Event event, String consumer);
}
