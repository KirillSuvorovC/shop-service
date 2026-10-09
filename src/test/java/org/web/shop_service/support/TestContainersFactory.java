package org.web.shop_service.support;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

public class TestContainersFactory {
    public static PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(
                "postgres:18-alpine")
                .withDatabaseName("test")
                .withUsername("test")
                .withPassword("test");
    }
    public static RabbitMQContainer rabbitMQContainer() {
        return new RabbitMQContainer("rabbitmq:4.3.5-management-alpine")
                .withAdminUser("test")
                .withAdminPassword("test");
    }
}
