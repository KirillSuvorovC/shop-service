package org.web.shop_service.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.web.shop_service.auth.CookieHelper;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ProfileTest
@Testcontainers
public class E2ETest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = TestContainersFactory.postgreSQLContainer();
    @Container
    @ServiceConnection
    static final RabbitMQContainer rabbitmq = TestContainersFactory.rabbitMQContainer();

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.port = port;
    }

    /**
     * @return JWT аутентифицированного админа
     */
    protected String authAsAdmin() {
        return givenWithCsrf()
                .when().post("/dev/api/auth/admin")
                .then()
                    .extract()
                    .cookie(CookieHelper.COOKIE_NAME_JWT);
    }

    protected RequestSpecification givenWithCsrf() {
        final String csrf = getCSRFToken();
        return given()
                .contentType(ContentType.JSON)
                .header("X-XSRF-TOKEN", csrf)
                .cookie("XSRF-TOKEN", csrf);
    }

    protected static String randomIdempotenceKey() {
        return UUID.randomUUID()
                .toString();
    }

    /**
     * Используется для проверки идемпотентности POST-запросов.
     * @param amount количество вызовов операции
     * @param operation сама операция
     * @return объект, полученный из последней выполненной операции.
     */
    protected <T> T repeatAndGetLast(int amount, Supplier<T> operation) {
        final int parallelOperationsAmount = amount - 1;
        final var start = new CountDownLatch(1);
        final var done = new CountDownLatch(parallelOperationsAmount);
        final var executor = Executors.newFixedThreadPool(parallelOperationsAmount);
        final Runnable request = () -> {
            try {
                start.await();
                operation.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                done.countDown();
            }
        };

        for (int i = 0; i < parallelOperationsAmount; i++) {
            executor.submit(request);
        }
        start.countDown();

        try {
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();

        return operation.get();
    }

    private String getCSRFToken() {
        return given()
                .when().get("/api/products")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .cookie("XSRF-TOKEN");
    }
}
