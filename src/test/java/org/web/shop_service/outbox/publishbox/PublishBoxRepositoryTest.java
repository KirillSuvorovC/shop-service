package org.web.shop_service.outbox.publishbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.support.TestContainersFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Testcontainers
class PublishBoxRepositoryTest {
    private static final String SQL_FILE = "test_data/insert_values.sql";
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();

    @Autowired
    private PublishBoxRepository repository;

    private static final String CONSUMER = "price-history";
    private static final UUID EXISTING_EVENT_ID = UUID.fromString(
                    "123e4567-e89b-12d3-a456-426614174000");
    private static final UUID NON_EXISTING_EVENT_ID = UUID.fromString(
                        "987e6543-e89b-12d3-a456-426614174999");

    @Test
    @Sql(SQL_FILE)
    @DisplayName("Есть существующая запись - должен вернуть true")
    void existsByOutboxEventIdAndConsumer_shouldReturnTrue_whenRecordExists() {
        // Action && Check
        final boolean exists = repository.existsByOutboxEventIdAndConsumer(
                EXISTING_EVENT_ID,
                CONSUMER
        );
        assertThat(exists).isTrue();
    }

    @Test
    @Sql(SQL_FILE)
    @DisplayName("Если запись не найдена - должен вернуть false")
    void existsByOutboxEventIdAndConsumer_shouldReturnFalse_whenRecordDoesNotExist() {
        // Action && Check
        boolean exists = repository.existsByOutboxEventIdAndConsumer(
                NON_EXISTING_EVENT_ID,
                CONSUMER
        );
        assertThat(exists).isFalse();
    }

    @Test
    @Sql(SQL_FILE)
    @DisplayName("""
                Если запись найдена, но с потребителем
                не совпадает - должен вернуть false
                """)
    void existsByOutboxEventIdAndConsumer_shouldReturnFalse_whenConsumerDoesNotMatch() {
        // Action && Check
        boolean exists = repository.existsByOutboxEventIdAndConsumer(
                EXISTING_EVENT_ID,
                "another-consumer"
        );
        assertThat(exists).isFalse();
    }
}
