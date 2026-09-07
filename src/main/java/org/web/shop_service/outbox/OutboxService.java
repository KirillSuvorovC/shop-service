package org.web.shop_service.outbox;

import java.util.List;

public interface OutboxService {
    void sendEvent(String exchange, String routingKey, Event event);
    List<OutboxEvent> getEventsBatch();
    void deleteEvents(List<OutboxEvent> events);
}