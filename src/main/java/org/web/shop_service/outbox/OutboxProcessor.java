package org.web.shop_service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxProcessor {
    private final OutboxService service;
    private final RabbitTemplate template;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendEventsInRabbitMQ() {
        final var events = service.getEventsBatch();
        if(events.isEmpty()) return;

        log.info("Processing {} outbox events", events.size());
        final var processedEvents = new ArrayList<OutboxEvent>();
        for (final OutboxEvent event : events) {
            try {
                template.convertAndSend(
                        event.getExchange(),
                        event.getRoutingKey(),
                        event.getPayload());
                processedEvents.add(event);
                log.debug("Event {} sent and marked processed", event.getId());
            } catch (Exception e) {
                log.error("Failed to send event {}: {}", event.getId(), e.getMessage());
            }
        }
        service.deleteEvents(processedEvents);
        log.info("Deleted processing outbox events: {}", processedEvents.size());
    }
}
