package org.web.shop_service.outbox.publishbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web.shop_service.outbox.Event;
import org.web.shop_service.outbox.publishbox.exceptions.PublishBoxException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishBoxServiceImpl implements PublishBoxService {
    private final PublishBoxRepository repository;

    public boolean existsEvent(UUID outboxEventId, String consumer) {
        return repository.existsByOutboxEventIdAndConsumer(
                outboxEventId, consumer);
    }

    public void publishEvent(Event event, String consumer) {
        final UUID outboxEventId = event.getEventId();
        if (existsEvent(outboxEventId, consumer)) {
            log.warn("Event {} already processed by consumer {}",
                                        outboxEventId, consumer);
            return;
        }

        final var publishBoxEvent = new PublishBoxEvent();
        publishBoxEvent.setOutboxEventId(outboxEventId);
        publishBoxEvent.setConsumer(consumer);
        try {
            repository.save(publishBoxEvent);
            log.info("Published event {} for consumer {}", outboxEventId, consumer);
        } catch (Exception e) {
            log.error("Failed to publish event {} for consumer {}: {}",
                    outboxEventId, consumer, e.getMessage(), e);
            throw new PublishBoxException("Failed to publish event", e);
        }
    }
}
