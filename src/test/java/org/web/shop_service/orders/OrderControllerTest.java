package org.web.shop_service.orders;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.web.shop_service.orders.support.TestOrderFactory;
import org.web.shop_service.support.configs.TestSecurityAOPConfig;
import org.web.shop_service.support.TestUserDefaults;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.utils.JsonUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(TestSecurityAOPConfig.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Nested
    @DisplayName("POST /api/orders — создание заказа")
    class Create {

        private static final String URI_TEMPLATE = "/api/orders";

        @Test
        @DisplayName("Возвращает 201 и DTO при валидном запросе")
        void shouldReturn201AndDto() throws Exception {
            // Given
            final var request = createRequest(1L, 1);
            final var order = TestOrderFactory.order(1L, TestUserDefaults.ENTITY_USER);
            when(orderService.create(any(), any())).thenReturn(new OrderDTO(order));
            // Action
            performRequest(HttpMethod.POST, URI_TEMPLATE, request,
                    TestUserDefaults.CURRENT_USER)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("NEW"))
                    .andExpect(jsonPath("$.items.length()")
                                    .value(request.items().size()));
        }

        @ParameterizedTest
        @MethodSource("invalidRequests")
        @DisplayName("Возвращает 400 при некорректном запросе")
        void shouldReturn400WhenInvalidRequest(OrderCreateRequest request) throws Exception {
            // Action && Check
            final var resultActions = performRequest(HttpMethod.POST, URI_TEMPLATE,
                                                     request,
                                                     TestUserDefaults.CURRENT_USER);
                    // Check
            checkError(resultActions, HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Возвращает 404, если продукт не найден")
        void shouldReturn404WhenProductNotFound() throws Exception {
            // Given
            final var request = createRequest(999L, 1);
            when(orderService.create(any(), any())).thenThrow(
                    new EntityNotFoundException("Product not found"));
            // Action
            final var resultActions = performRequest(HttpMethod.POST, URI_TEMPLATE,
                                                     request,
                                                     TestUserDefaults.CURRENT_USER);
            checkError(resultActions, HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Возвращает 401 без аутентификации")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            final var request = createRequest(1L, 1);
            // Action
            final var resultActions = mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request))
                            .with(csrf()));
            checkError(resultActions, HttpStatus.UNAUTHORIZED);
        }

        static Stream<Arguments> invalidRequests() {
            return Stream.of(
                    arguments(new OrderCreateRequest(null)),
                    arguments(createRequest(1L, null)),
                    arguments(createRequest(1L, -1)),
                    arguments(createRequest(null, 1))
            );
        }

        private static OrderCreateRequest createRequest(Long productId, Integer amount) {
            final var item = new OrderCreateRequest.Item(productId, amount);
            return new OrderCreateRequest(List.of(item));
        }
    }

    @Nested
    @DisplayName("PUT /api/orders/{id}/status — обновление статуса")
    class UpdateStatus {
        private static final String URI_TEMPLATE = "/api/orders/1/status";

        @Test
        @DisplayName("Возвращает 200 и DTO при валидном запросе")
        void shouldReturn200AndDto() throws Exception {
            // Given
            final var request = createRequest();
            final var order = TestOrderFactory.order(1L,
                            TestUserDefaults.ENTITY_ADMIN);
            order.setStatus(OrderStatus.CANCELLED);
            final String orderVersion = order.getVersion().toString();
            when(orderService.updateStatus(anyLong(), any())).thenReturn(new OrderDTO(order));
            // Action
            performRequestWithIfMatch(request,
                                      TestUserDefaults.CURRENT_ADMIN,
                                      orderVersion)
                    // Check
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value(request.getStatus()));
        }

        @Test
        @DisplayName("Возвращает 400 при невалидном статусе")
        void shouldReturn400WhenInvalidStatus() throws Exception {
            // Given
            final var request = new OrderUpdateStatusRequest("unknown");
            // Action && Check
            final var resultActions = performRequestWithIfMatch(request,
                                        TestUserDefaults.CURRENT_ADMIN,
                                        "1");
            checkError(resultActions, HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Возвращает 404, если заказ не найден")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            // Given
            final var request = createRequest();
            when(orderService.updateStatus(anyLong(), any()))
                    .thenThrow(new EntityNotFoundException("Order not found"));
            // Action && Check
            final var resultActions = performRequestWithIfMatch(request,
                                        TestUserDefaults.CURRENT_ADMIN,
                                        "1");
            checkError(resultActions, HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Возвращает 409 при конфликте версий")
        void shouldReturn409WhenVersionConflict() throws Exception {
            // Given
            final var request = createRequest();
            when(orderService.updateStatus(anyLong(), any()))
                    .thenThrow(new OptimisticLockingFailureException("Data old, reload page"));
            // Action && Check
            final var resultActions = performRequestWithIfMatch(request,
                                        TestUserDefaults.CURRENT_ADMIN,
                                        "1");
            checkError(resultActions, HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("Возвращает 403 если пользователь - не админ")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            // Given
            final var request = createRequest();
            // Action && Check
            performRequestWithIfMatch(request,
                    TestUserDefaults.CURRENT_USER,
                    "1")
                    .andExpect(status().isForbidden());
        }

        private static OrderUpdateStatusRequest createRequest() {
            return new OrderUpdateStatusRequest(OrderStatus.CANCELLED.name());
        }

        private ResultActions performRequestWithIfMatch(Object requestBody,
                                                        AuthUser authUser,
                                                        String ifMatch)
                                                        throws Exception {
            return mockMvc.perform(put(URI_TEMPLATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.asJson(requestBody))
                    .header(HttpHeaders.IF_MATCH, ifMatch)
                    .with(csrf())
                    .with(user(authUser)));
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{id} — поиск по ID")
    class FindById {
        public static final String URI_TEMPLATE = "/api/orders/1";

        @Test
        @DisplayName("Возвращает 200 и DTO, если заказ найден")
        void shouldReturn200AndDto() throws Exception {
            // Given
            final var order = TestOrderFactory.order(1L,
                            TestUserDefaults.ENTITY_ADMIN);
            when(orderService.findById(1L)).thenReturn(new OrderDTO(order));
            // Action
            performGetRequest(URI_TEMPLATE, TestUserDefaults.CURRENT_ADMIN)
                    // Check
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value(order.getStatus().name()))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.userId").doesNotExist());
        }

        @Test
        @DisplayName("Возвращает 404, если заказ не найден")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(orderService.findById(1L))
                    .thenThrow(new EntityNotFoundException("Order not found"));
            // Action && Check
            final var resultActions = performGetRequest(URI_TEMPLATE,
                                    TestUserDefaults.CURRENT_ADMIN);
            checkError(resultActions, HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Возвращает 403 если пользователь - не админ")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            // Action && Check
            performGetRequest(URI_TEMPLATE, TestUserDefaults.CURRENT_USER)
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/orders — заказы пользователя")
    class FindAllByUserId {

        private static final String URI_TEMPLATE = "/api/orders";

        @Test
        @DisplayName("Возвращает 200 и страницу заказов")
        void shouldReturn200AndPage() throws Exception {
            // Given
            final var request = PageRequest.of(0, 20);
            final var order = TestOrderFactory.order(1L,
                            TestUserDefaults.ENTITY_ADMIN);
            when(orderService.findAllByUserId(any(), any()))
                    .thenReturn(new OrdersDTO(List.of(order)));
            // Action
            performRequest(HttpMethod.GET, URI_TEMPLATE, request,
                                    TestUserDefaults.CURRENT_USER)
                    // Check
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orders[0].id").value(order.getId()))
                    .andExpect(jsonPath("$.orders[0].status")
                                        .value(order.getStatus().name()))
                    .andExpect(jsonPath("$.orders.length()").value(1))
                    .andExpect(jsonPath("$.count").value(1));
        }

        @Test
        @DisplayName("Использует размер страницы по умолчанию 20")
        void shouldUseDefaultPageSize() throws Exception {
            // Given
            final var atomicPageable = new AtomicReference<Pageable>();
            when(orderService.findAllByUserId(any(), any()))
                    .thenAnswer(invocation -> {
                        atomicPageable.set(invocation.getArgument(1, Pageable.class));
                        return null;
                    });
            // Action
            performGetRequest(URI_TEMPLATE, TestUserDefaults.CURRENT_USER);
            // Check
            final var resultPageable = atomicPageable.get();
            assertThat(resultPageable.getPageSize()).isEqualTo(20);
        }

        @Test
        @DisplayName("Возвращает 401 без аутентификации")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            final var resultActions = mockMvc.perform(get(URI_TEMPLATE)
                                                     .with(csrf()));
            checkError(resultActions, HttpStatus.UNAUTHORIZED);
        }
    }

    private static void checkError(ResultActions actions, HttpStatus expectedStatus)
                                                                    throws Exception {
        final int statusCode = expectedStatus.value();
        actions.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").exists());
    }

    private ResultActions performRequest(HttpMethod method, String uriTemplate,
                                Object requestBody, AuthUser authUser)
            throws Exception {
        return mockMvc.perform(request(method, uriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJson(requestBody))
                .with(csrf())
                .with(user(authUser)));
    }

    private ResultActions performGetRequest(String uriTemplate, AuthUser authUser)
            throws Exception {
        return mockMvc.perform(get(uriTemplate)
                .with(csrf())
                .with(user(authUser)));
    }
}
