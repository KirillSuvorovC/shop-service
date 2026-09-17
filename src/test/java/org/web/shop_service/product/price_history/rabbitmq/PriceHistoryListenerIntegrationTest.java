package org.web.shop_service.product.price_history.rabbitmq;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.TestContainersFactory;
import org.web.shop_service.TestRabbitMQConfig;
import org.web.shop_service.outbox.Event;
import org.web.shop_service.outbox.publishbox.PublishBoxRepository;
import org.web.shop_service.outbox.publishbox.PublishBoxService;
import org.web.shop_service.product.ProductRepository;
import org.web.shop_service.product.events.ProductPriceChangedEvent;
import org.web.shop_service.product.price_history.ProductPriceHistoryRepository;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@Import(TestRabbitMQConfig.class)
@Sql("/org/web/shop_service/product/insert_values.sql")
class PriceHistoryListenerIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();

    @Autowired
    private TestRabbitTemplate template;
    @Autowired
    private PriceHistoryRabbitMQProperties rabbitMQProperties;

    @Autowired
    private ProductPriceHistoryRepository priceHistoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PublishBoxRepository publishBoxRepository;

    @MockitoSpyBean
    private PublishBoxService publishBoxService;

    private final ProductPriceChangedEvent event = new ProductPriceChangedEvent(
            1L,
            new BigDecimal("30.00"),
            ZonedDateTime.now(ZoneOffset.UTC));

    @AfterEach
    void cleanUp() {
        priceHistoryRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("""
                Слушатель должен успешно сохранить изменение цены
                после получения события из очереди
                """)
    void shouldSavePriceHistoryWhenMessageReceived() {
        // Action
        template.convertAndSend(rabbitMQProperties.getQueuePriceChanged(), event);
        // Check
        final var histories = priceHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getPrice()).isEqualByComparingTo(event.getNewPrice());
    }

    @Test
    @DisplayName("""
                Должен сохранить изменение цены один раз,
                если пришёл дубликат события
                """)
    void shouldSavePriceHistoryOnce() {
        // Action
        template.convertAndSend(rabbitMQProperties.getQueuePriceChanged(), event);
        template.convertAndSend(rabbitMQProperties.getQueuePriceChanged(), event);
        // Check
        final var histories = priceHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getPrice()).isEqualByComparingTo(event.getNewPrice());
    }

    @Test
    @DisplayName("""
                Цена продукта не должна сохраниться в БД и событие не
                будет обработано, если слушатель выбросит исключение
                """)
    void shouldRollbackSavingPriceHistory_whenThrowException() {
        // Given
        doThrow(new RuntimeException("DB down"))
                .when(publishBoxService).publishEvent(any(), any());
        // Action
        assertThatThrownBy(() ->
                template.convertAndSend(rabbitMQProperties.getQueuePriceChanged(), event));
        // Check
        assertThat(priceHistoryRepository.findAll()).isEmpty();
        assertThat(publishBoxRepository.findAll()).isEmpty();
    }
}
