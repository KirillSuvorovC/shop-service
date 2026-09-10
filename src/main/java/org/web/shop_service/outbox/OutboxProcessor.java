package org.web.shop_service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class OutboxProcessor {
    private final OutboxService service;
    private final RabbitTemplate template;

    @Scheduled(fixedDelayString = "${app.outbox.interval-send-events}")
    @Transactional
    public void sendEventsInRabbitMQ() {
        final var events = service.getEventsBatch();
        if(events.isEmpty()) return;

        log.info("Processing {} outbox events", events.size());
        final var processedEvents = new ArrayList<OutboxEvent>();
        for (final OutboxEvent event : events) {
            try {
                final var message = new Message(event.getPayload()
                                .getBytes(StandardCharsets.UTF_8));
                message.getMessageProperties().setContentType("application/json");
                template.send(
                        event.getExchange(),
                        event.getRoutingKey(),
                        message);
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
