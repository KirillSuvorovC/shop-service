package org.web.shop_service.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.web.shop_service.utils.JsonUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OutboxServiceImpl implements OutboxService {
    private final OutBoxRepository repository;
    @Value("${app.outbox.events-batch-size}")
    private int batchSize;

    @Override
    public void sendEvent(String exchange, String routingKey, Event event) {

        final var outboxEvent = new OutboxEvent();
        outboxEvent.setExchange(exchange);
        outboxEvent.setRoutingKey(routingKey);
        outboxEvent.setPayload(JsonUtils.asJson(event));
        repository.save(outboxEvent);
    }

    @Override
    public List<OutboxEvent> getEventsBatch() {
        final var request = PageRequest.of(0, batchSize);
        return repository.findAll(request)
                         .getContent();
    }

    @Override
    public void deleteEvents(List<OutboxEvent> events) {
        repository.deleteAllInBatch(events);
    }
}
