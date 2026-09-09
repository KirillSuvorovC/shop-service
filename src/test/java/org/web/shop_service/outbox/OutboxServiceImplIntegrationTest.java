package org.web.shop_service.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.TestContainersFactory;
import org.web.shop_service.MockRabbitMQConfig;
import org.web.shop_service.utils.JsonUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Import(MockRabbitMQConfig.class)
@Testcontainers
class OutboxServiceImplIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();

    @Autowired
    private OutboxService service;

    @Autowired
    private OutBoxRepository repository;

    @Test
    void shouldSaveOutboxEventToDatabase() {
        // Given
        final var event = new Event();
        final String exchange = "exchange";
        final String routingKey = "routing_key";
        final String payload = JsonUtils.asJson(event);
        // Action
        service.sendEvent(exchange, routingKey, event);
        // Check
        final var events = repository.findAll();
        final var actualEvent = events.get(0);
        assertThat(events).hasSize(1);
        assertThat(actualEvent.getExchange()).isEqualTo(exchange);
        assertThat(actualEvent.getRoutingKey()).isEqualTo(routingKey);
        assertThat(actualEvent.getPayload()).isEqualTo(payload);
    }
}
