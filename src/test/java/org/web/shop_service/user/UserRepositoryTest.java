package org.web.shop_service.user;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.TestContainersFactory;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Testcontainers
public class UserRepositoryTest {
    @ServiceConnection
    @Container
    static final PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();

    @Autowired
    private UserRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Вернуть существующего пользователя")
    void findByUsernameWithRole_shouldNotTriggerNPlusOne() {
        entityManager.clear();
        final var stats = entityManager.getEntityManagerFactory()
                                            .unwrap(SessionFactory.class)
                                            .getStatistics();
        stats.clear();
        final var user = repository.findByUsernameWithRole("admin").orElseThrow();
        user.getRole().getName();
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }
    @Test
    @DisplayName("Вернуть существующего пользователя")
    void findByUsernameWithRole_shouldReturnNotEmptyIfExists() {
        assertThat(repository.findByUsernameWithRole("admin")).isPresent();
    }

    @Test
    @DisplayName("Вернуть несуществующего пользователя")
    void findByUsernameWithRole_shouldReturnEmptyIfNonExists() {
        assertThat(repository.findByUsernameWithRole("unknown")).isNotPresent();
    }

    @Test
    @DisplayName("Вернуть true, если пользователь существует")
    void existsByUsername_shouldReturnTrueIfExists() {
        assertThat(repository.existsByUsername("admin")).isTrue();
    }

    @Test
    @DisplayName("Вернуть false, если пользователь не существует")
    void existsByUsername_shouldReturnFalseIfNonExists() {
        assertThat(repository.existsByUsername("unknown")).isFalse();
    }
}
