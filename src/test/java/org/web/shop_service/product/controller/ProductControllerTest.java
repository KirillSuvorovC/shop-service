package org.web.shop_service.product.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.web.shop_service.product.*;
import org.web.shop_service.product.support.TestProductFactory;
import org.web.shop_service.support.RestControllerTest;
import org.web.shop_service.support.TestUserDefaults;
import org.web.shop_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.web.shop_service.utils.JsonUtils.asJson;

@RestControllerTest(controllers = ProductController.class)
class ProductControllerTest {
    @MockitoBean
    private ProductService service;
    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("POST /api/products - создание продукта")
    class CreateProduct {
        @Test
        @DisplayName("Должен создать продукт и вернуть 201")
        void shouldReturn201AndDto() throws Exception {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            final var dto = ProductDTO.fromProduct(product);
            final var requestJson = asJson(new ProductCreateRequest("Product",
                    product.getPrice()));
            when(service.createProduct(any())).thenReturn(dto);
            // Action && Check
            final var resultActions = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf())
                    .with(user(TestUserDefaults.CURRENT_ADMIN)));
            expectProduct(resultActions, HttpStatus.CREATED, product);
        }

        @ParameterizedTest
        @MethodSource("invalidArguments")
        @DisplayName("При некорректном запросе вернуть 400")
        void whenInvalidRequest_shouldReturn400(String productName,
                                                BigDecimal productPrice)
                throws Exception {
            // Given
            final var request = new ProductCreateRequest(productName, productPrice);
            // Action
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request))
                            .with(csrf())
                            .with(user(TestUserDefaults.CURRENT_ADMIN)))
                    // Check
                    .andExpect(status().isBadRequest());
        }

        static Stream<Arguments> invalidArguments() {
            return ProductControllerTest.invalidArguments();
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id} - полное обновление продукта")
    class UpdateProduct {
        @Test
        @DisplayName("Должен обновить продукт и вернуть 200")
        void shouldReturn200AndDto() throws Exception {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            final var dto = ProductDTO.fromProduct(product);
            final var requestJson = asJson(new ProductUpdateRequest("Product",
                    product.getPrice()));
            when(service.updateProduct(any(), any())).thenReturn(dto);
            // Action && Check
            final var resultActions = mockMvc.perform(put("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .header(HttpHeaders.IF_MATCH, "1")
                    .with(csrf())
                    .with(user(TestUserDefaults.CURRENT_ADMIN)));
            expectProduct(resultActions, HttpStatus.OK, product);
        }

        @ParameterizedTest
        @MethodSource("invalidArguments")
        @DisplayName("Если запрос некорректный - вернуть 400")
        void whenInvalidRequest_shouldReturn400(String productName,
                                                BigDecimal productPrice)
                throws Exception {
            // Given
            final var request = new ProductUpdateRequest(productName, productPrice);
            // Action
            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request))
                            .header(HttpHeaders.IF_MATCH, "1")
                            .with(csrf())
                            .with(user(TestUserDefaults.CURRENT_ADMIN)))
                    // Check
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @MethodSource("optimisticLockExceptions")
        @DisplayName("Если произошла оптимистичная блокировка - вернуть 409")
        void whenVersionNotMatch_shouldReturn409(Exception exception)
                throws Exception {
            // Given
            final var request = new ProductUpdateRequest("Product",
                    new BigDecimal("50.00"));
            when(service.updateProduct(any(), any())).thenThrow(exception);
            // Action
            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request))
                            .header(HttpHeaders.IF_MATCH, "1")
                            .with(csrf())
                            .with(user(TestUserDefaults.CURRENT_ADMIN)))
                    // Check
                    .andExpect(status().isConflict());
        }

        static Stream<Arguments> invalidArguments() {
            return ProductControllerTest.invalidArguments();
        }
        static Stream<Arguments> optimisticLockExceptions() {
            return ProductControllerTest.optimisticLockExceptions();
        }
    }

    @Nested
    @DisplayName("PATCH /api/products/{id} - частичное обновление продукта")
    class PatchProduct {
        @ParameterizedTest
        @MethodSource("validNullArguments")
        @DisplayName("Должен вернуть 200 и DTO")
        void shouldReturn200AndDto(String productName,
                                   BigDecimal productPrice)
                throws Exception {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            final var request = new ProductPatchRequest(productName, productPrice);
            final var requestJson = asJson(request);
            when(service.patchProduct(any(), any())).thenReturn(ProductDTO
                    .fromProduct(product));
            // Action && Check
            var resultActions = mockMvc.perform(patch("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .header(HttpHeaders.IF_MATCH, "1")
                    .with(csrf())
                    .with(user(TestUserDefaults.CURRENT_ADMIN)));
            expectProduct(resultActions, HttpStatus.OK, product);
        }

        @Test
        @DisplayName("При некорректном запросе вернуть 400")
        void whenInvalidRequest_shouldReturn400()
                throws Exception {
            // Given
            final var request = new ProductPatchRequest("n", new BigDecimal("-1"));
            when(service.patchProduct(any(), any())).thenThrow(
                    new ValidationException("Validation failed"));
            // Action
            mockMvc.perform(patch("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request))
                            .header(HttpHeaders.IF_MATCH, "1")
                            .with(csrf())
                            .with(user(TestUserDefaults.CURRENT_ADMIN)))
                    // Check
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @MethodSource("optimisticLockExceptions")
        @DisplayName("Если произошла оптимистичная блокировка - вернуть 409")
        void whenVersionNotMatch_shouldReturn409(Exception exception)
                throws Exception {
            // Given
            final var request = new ProductUpdateRequest("Product",
                    new BigDecimal("50.00"));
            when(service.updateProduct(any(), any())).thenThrow(exception);
            // Action
            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request))
                            .header(HttpHeaders.IF_MATCH, "1")
                            .with(csrf())
                            .with(user(TestUserDefaults.CURRENT_ADMIN)))
                    // Check
                    .andExpect(status().isConflict());
        }

        static Stream<Arguments> validNullArguments() {
            return ProductControllerTest.validNullArguments();
        }
        static Stream<Arguments> optimisticLockExceptions() {
            return ProductControllerTest.optimisticLockExceptions();
        }
    }

    @Nested
    @DisplayName("GET - /api/products/{id} - получение продукта по ID")
    class GetProductById {
        @Test
        @DisplayName("Вернуть продукт и 200")
        void getProductById_shouldReturn200AndDto() throws Exception {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            when(service.getProductById(1L)).thenReturn(ProductDTO.fromProduct(product));
            // Action && Check
            final var resultActions = mockMvc.perform(get("/api/products/1"));
            expectProduct(resultActions, HttpStatus.OK, product);
        }

        @Test
        @DisplayName("Вернуть 404, если продукт не найден")
        void getProductById_whenProductNonExists_shouldReturn404() throws Exception {
            // Given
            when(service.getProductById(1L)).thenThrow(
                    new EntityNotFoundException("Product not found"));
            // Action && Check
            mockMvc.perform(get("/api/products/1"))
                    .andExpect(status().isNotFound());
        }

    }

    @Test
    @DisplayName("Вернуть продукты и 200")
    void shouldReturn200AndDto() throws Exception {
        // Given
        final var products = List.of(
                TestProductFactory.createProduct(1L),
                TestProductFactory.createProduct(2L),
                TestProductFactory.createProduct(3L)
        );
        final var request = PageRequest.of(0, 20);
        when(service.getProducts(any())).thenReturn(new ProductsDTO(products));
        // Action
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                // Check
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(products.size()))
                .andExpect(jsonPath("$.count").value(products.size()));

    }

    @Test
    @DisplayName("Должен удалить продукт и вернуть 204")
    void deleteProductById_shouldReturn204() throws Exception {
        // Action && Check
        mockMvc.perform(delete("/api/products/1")
                        .with(csrf())
                        .with(user(TestUserDefaults.CURRENT_ADMIN)))
                .andExpect(status().isNoContent());
    }

    static Stream<Arguments> validNullArguments() {
        return Stream.of(
                arguments("Product", null),
                arguments(null, new BigDecimal("50.00")),
                arguments(null, null)
        );
    }
    static Stream<Arguments> invalidArguments() {
        return Stream.of(
                arguments("Product", new BigDecimal("-1")),
                arguments("n", new BigDecimal("50.00")),
                arguments("n", new BigDecimal("-1")),
                arguments(null, new BigDecimal("-1")),
                arguments("n", null),
                arguments(null, null)
        );
    }

    static Stream<Arguments> optimisticLockExceptions() {
        return Stream.of(
                arguments(new OptimisticLockException()),
                arguments(new OptimisticLockingFailureException("Data is old, reload page"))
        );
    }

    void expectProduct(ResultActions actions,
                       HttpStatus expectedStatus,
                       Product expected) throws Exception {
        actions.andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.price").value(expected.getPrice()
                        .doubleValue()));
    }
}
