package org.web.shop_service.outbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.core.Message;
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
import org.web.shop_service.utils.JsonUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@Transactional
@Import(MockRabbitMQConfig.class)
public class OutboxProcessorIntegrationTest {
    public static final String EXCHANGE = "exchange";
    public static final String ROUTING_KEY = "routing_key";
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

    @Captor
    private ArgumentCaptor<String> exchangeCaptor;
    @Captor
    private ArgumentCaptor<String> routingKeyCaptor;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;

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
        verify(rabbitTemplate, times(1)).send(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                messageCaptor.capture());

        final var message = messageCaptor.getValue();
        final String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        final String contentType = message.getMessageProperties().getContentType();
        assertThat(payload).isEqualTo(JsonUtils.asJson(event));
        assertThat(contentType).isEqualTo("application/json");
        assertThat(exchangeCaptor.getValue()).isEqualTo(EXCHANGE);
        assertThat(routingKeyCaptor.getValue()).isEqualTo(ROUTING_KEY);
    }
}
