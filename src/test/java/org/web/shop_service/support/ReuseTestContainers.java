package org.web.shop_service.support;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Objects;

public class ReuseTestContainers {
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();
    @ServiceConnection
    static final RabbitMQContainer rabbitmq = TestContainersFactory.rabbitMQContainer();

    static {
        Startables.deepStart(postgres, rabbitmq).join();
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;


    // К дополнению сбросить последовательности
    private static final String SQL_CLEAN_TABLES =
            "select 'truncate table ' || " +
                    "string_agg(tablename, ', ') || " +
                    "' restart identity cascade;' " +
                    "from pg_tables " +
                    "where not tablename in ('flyway_schema_history', 'roles', 'users') " +
                    "and schemaname = 'public';";
    @AfterEach
    void cleanDatabase() {
        final String statement = jdbcTemplate.queryForObject(SQL_CLEAN_TABLES, String.class);
        Objects.requireNonNull(statement);
        jdbcTemplate.execute(statement);
    }
}
