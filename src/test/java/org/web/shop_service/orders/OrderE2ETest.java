package org.web.shop_service.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.http.HttpStatus;
import org.web.shop_service.auth.CookieHelper;
import org.web.shop_service.openapi.CustomHTTPHeaders;
import org.web.shop_service.product.ProductCreateRequest;
import org.web.shop_service.product.ProductDTO;
import org.web.shop_service.support.E2ETest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class OrderE2ETest extends E2ETest {
    private static final String IDEMPOTENCY_ERROR = """
                                \n
                                POST-запрос неидемпотентный. Ожидалось,
                                что запрос вернет тот же ответ, что и при
                                первой отправке запроса
                                """;
    private static final String PRODUCT_IDEMPOTENCY_KEY = randomIdempotenceKey();
    private static final String ORDER_IDEMPOTENCY_KEY = randomIdempotenceKey();

    @Test
    @DisplayName("""
            Проверить цепочку и ее идемпотентность:\s
            аутентификация как админ ->\s
            создание продукта ->\s
            оформление заказа этого продукта
            """)
    @Execution(ExecutionMode.SAME_THREAD)
    void orderShouldBeCreatedOnce() {
        // Given
        final String jwt = authAsAdmin();

        final var product = sendProductCreateRequest(jwt);
        final Long productId = product.getId();
        final Integer productAmount = 5;
        // Action
        final var order = sendOrderCreateRequest(jwt, productId, productAmount);
        final var orderItem = order.getItems().get(0);

        // Check
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getStatus()).isEqualByComparingTo(OrderStatus.NEW);
        assertThat(orderItem.productId()).isEqualTo(productId);
        assertThat(orderItem.amount()).isEqualTo(productAmount);
    }

    private ProductDTO sendProductCreateRequest(String jwt) {
        final var productCreateRequest = new ProductCreateRequest(
                "Product", new BigDecimal("50.00"));
        final int repeatAmount = 8;

        final var response = repeatAndGetLast(repeatAmount, () -> {
            return givenWithCsrf()
                    .header(CustomHTTPHeaders.IDEMPOTENCY_KEY, PRODUCT_IDEMPOTENCY_KEY)
                    .cookie(CookieHelper.COOKIE_NAME_JWT, jwt)
                    .body(productCreateRequest)
                    .when().post("/api/products")
                    .then().assertThat()
                        .statusCode(HttpStatus.CREATED.value())
                        .onFailMessage(IDEMPOTENCY_ERROR)
                        .body("id", equalTo(1));
        });
        return response.extract()
                .as(ProductDTO.class);
    }

    private OrderDTO sendOrderCreateRequest(String jwt,
                                            Long productId, Integer productAmount) {
        final var orderCreateRequest = new OrderCreateRequest(List.of(
                new OrderCreateRequest.Item(productId, productAmount)
        ));

        final int repeatAmount = 8;
        final var response = repeatAndGetLast(repeatAmount, () -> {
            return givenWithCsrf()
                    .header(CustomHTTPHeaders.IDEMPOTENCY_KEY, ORDER_IDEMPOTENCY_KEY)
                    .cookie(CookieHelper.COOKIE_NAME_JWT, jwt)
                    .body(orderCreateRequest)
                    .when().post("/api/orders")
                    .then().assertThat()
                        .statusCode(HttpStatus.CREATED.value())
                        .onFailMessage(IDEMPOTENCY_ERROR)
                        .body("id", equalTo(1));
        });
        return response.extract()
                .as(OrderDTO.class);
    }
}
