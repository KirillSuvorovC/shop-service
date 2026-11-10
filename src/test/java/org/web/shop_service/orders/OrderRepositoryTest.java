package org.web.shop_service.orders;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.support.ReuseTestContainers;
import org.web.shop_service.support.TestContainersFactory;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest(properties =
        "spring.jpa.properties.hibernate.generate_statistics=true")
@Sql(OrderRepositoryTest.SQL_FILE)
class OrderRepositoryTest extends ReuseTestContainers {
    static final String SQL_FILE = "insert_values.sql";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldNotTriggerNPlusOne() {
        // Given
        entityManager.clear();
        final var sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.clear();

        // Action
        final var orders = orderRepository.findAllByUserId(1L,
                        PageRequest.of(0, 1));
        orders.get(0).getItems().get(0).getProduct().getId();

        // Check
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }
}
