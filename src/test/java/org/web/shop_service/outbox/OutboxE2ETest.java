package org.web.shop_service.outbox;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.support.TestContainersFactory;
import org.web.shop_service.outbox.publishbox.PublishBoxService;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Import(OutboxE2ETest.TestPublishBoxListener.class)
@Testcontainers
class OutboxE2ETest {

    public static final String TEST_LISTENER_ID = "testListener";

    @RequiredArgsConstructor
    @TestComponent
    static class TestPublishBoxListener {
        private final PublishBoxService publishBoxService;

        @RabbitListener(
                queues = QUEUE,
                autoStartup = "false",
                id = TEST_LISTENER_ID
        )
        public void handle(Event event) {
            publishBoxService.publishEvent(event, CONSUMER);
        }
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();
    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = TestContainersFactory.rabbitMQContainer();

    /**
     * Устанавливаем маленькую задержку интервала отправки событий
     * сервисом {@link OutboxProcessor} для ускорения теста
     */
    @DynamicPropertySource
    static void configureInterval(DynamicPropertyRegistry registry) {
        // В миллисекундах
        final Supplier<Object> value = () -> 500;
        registry.add("app.outbox.interval-send-events", value);
    }

    public static final String ROUTING_KEY = "event.created.test";
    public static final String QUEUE = "queue.test";
    public static final String EXCHANGE = "event.exchange.test";
    public static final String CONSUMER = "price-history";

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TestPublishBoxListener listener;
    @Autowired
    private RabbitListenerEndpointRegistry registry;

    @Autowired
    private PublishBoxService publishBoxService;
    @Autowired
    private OutboxService outboxService;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @BeforeEach
    void setUp() {
        final DirectExchange directExchange = ExchangeBuilder
                                        .directExchange(EXCHANGE)
                                        .durable(true)
                                        .autoDelete()
                                        .build();
        final var queue = QueueBuilder.durable(QUEUE)
                                        .autoDelete()
                                        .build();

        final var binding = BindingBuilder
                                        .bind(queue)
                                        .to(directExchange)
                                        .with(ROUTING_KEY);

        rabbitAdmin.declareExchange(directExchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
        registry.getListenerContainer(TEST_LISTENER_ID).start();
    }

    @Test
    @DisplayName("""
            Событие должно успешно дойти по цепочке:
            OutboxService -> OutboxProcessor -> RabbitListener -> PublishBoxService
            """)
    void shouldProcessOutboxEventAndSaveToPublishBox() {
        // Action
        final var event = new Event();
        outboxService.sendEvent(EXCHANGE, ROUTING_KEY, event);
        // Check
        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    final boolean exists = publishBoxService.existsEvent(
                                            event.getEventId(), CONSUMER);
                    assertThat(exists).isTrue();
                });

    }
}
