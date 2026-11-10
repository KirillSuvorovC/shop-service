package org.web.shop_service.outbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.support.ProfileTest;
import org.web.shop_service.support.ReuseTestContainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ProfileTest
@Testcontainers
@Transactional
public class OutboxProcessorIntegrationTest extends ReuseTestContainers {
    public static final String EXCHANGE = "exchange";
    public static final String ROUTING_KEY = "routing_key";

    @Autowired
    private OutboxService outboxService;
    @Autowired
    private OutBoxRepository outboxRepository;
    @Autowired
    private OutboxProcessor processor;

    @Test
    @DisplayName("""
            Сервис OutboxProcessor должен отправить событие,
            если есть, затем удалить отправленное событие
            """)
    void shouldSaveEventAndSendToRabbitMQ() {
        // Given
        final var event = new Event();

        // Actions
        outboxService.sendEvent(EXCHANGE, ROUTING_KEY, event);
        processor.sendEventsInRabbitMQ();

        // Check
        final var events = outboxRepository.findAll();
        assertThat(events).isEmpty();
    }
}
