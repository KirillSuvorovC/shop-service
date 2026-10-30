package org.web.shop_service.product.controller;

import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.web.shop_service.idempotence.IdempotenceKeyService;
import org.web.shop_service.openapi.CustomHTTPHeaders;
import org.web.shop_service.product.ProductController;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.support.RestControllerTest;
import org.web.shop_service.support.TestUserDefaults;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestControllerTest(controllers = ProductController.class)
class ProductControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;
    @MockitoBean
    private IdempotenceKeyService idempotenceKeyService;

    @MockitoBean
    private Validator validator;

    @ParameterizedTest
    @MethodSource("urls")
    @DisplayName("Вернуть 401, если пользователь не аутентифицирован")
    void shouldReturn401_whenUserUnauthorized(HttpMethod method,
                                              String uriTemplate)
            throws Exception {
        // Action && Check
        final var requestBuilder = request(method, uriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header(HttpHeaders.IF_MATCH, "1")
                .with(csrf());
        if(HttpMethod.POST.matches(method.name()))
            requestBuilder.header(CustomHTTPHeaders.IDEMPOTENCY_KEY, UUID.randomUUID());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("urls")
    @DisplayName("Вернуть 403, если пользователь аутентифицирован, но не админ")
    void shouldReturn403_whenUserIsNotAdmin(HttpMethod method,
                                            String uriTemplate)
            throws Exception {
        // Action && Check
        final var requestBuilder = request(method, uriTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header(HttpHeaders.IF_MATCH, "1")
                .with(csrf())
                .with(user(TestUserDefaults.CURRENT_USER));
        if(HttpMethod.POST.matches(method.name()))
            requestBuilder.header(CustomHTTPHeaders.IDEMPOTENCY_KEY, UUID.randomUUID());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    public static Stream<Arguments> urls() {
        // HTTPMethod | uriTemplate
        return Stream.of(
                arguments(HttpMethod.POST, "/api/products"),
                arguments(HttpMethod.PUT, "/api/products/1"),
                arguments(HttpMethod.PATCH, "/api/products/1"),
                arguments(HttpMethod.DELETE, "/api/products/1")
        );
    }
}
