package org.web.shop_service.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.TestContainersFactory;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Sql(ProductRepositoryTest.SQL_FILE)
public class ProductRepositoryTest {
    public static final String SQL_FILE = "insert_values.sql";
    @ServiceConnection
    @Container
    static final PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();

    @Autowired
    private ProductRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("""
                Архивизирует продукт вместо физического удаления.
                При этом SELECT-запросы не возвращают архивизированные
                продукты
                """)
    void shouldMarkProductAsArchived() {
        // Given
        final String query ="""
                            select count(*) from %s
                            where product_id = 1 and archived = true
                            """.formatted(Product.TABLE_NAME);

        // Action
        repository.markProductAsArchived(1L);

        // Check
        final Integer archivedCount = jdbcTemplate.queryForObject(query, Integer.class);
        final var archivedProduct = repository.findById(1L);

        assertThat(archivedProduct).isNotPresent();
        assertThat(archivedCount).isEqualTo(1);
    }
}
