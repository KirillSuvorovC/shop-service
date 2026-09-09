package org.web.shop_service.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.MockRabbitMQConfig;
import org.web.shop_service.TestContainersFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@Transactional
@Import(MockRabbitMQConfig.class)
public class OutboxProcessorIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();

    @MockitoBean
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OutboxService outboxService;
    @Autowired
    private OutBoxRepository outboxRepository;
    @Autowired
    private OutboxProcessor processor;

    @Test
    void shouldSaveEventAndSendToRabbitMQ() {
        // Given
        final String exchange = "exchange";
        final String routingKey = "routing_key";
        final var event = new Event();

        // Actions
        outboxService.sendEvent(exchange, routingKey, event);
        processor.sendEventsInRabbitMQ();

        // Check
        final var events = outboxRepository.findAll();
        assertThat(events).isEmpty();
        verify(rabbitTemplate, times(1))
                .convertAndSend(any(), any(), (Event)any());
    }
}
