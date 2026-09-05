package org.web.shop_service.product;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.web.shop_service.TestSecurityConfig;
import org.web.shop_service.utils.JsonUtils;
import org.web.shop_service.utils.exceptions.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.web.shop_service.utils.JsonUtils.asJson;

@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = TestSecurityConfig.class)
class ProductControllerTest {
    @MockitoBean
    private ProductService service;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_shouldReturn201AndDto() throws Exception {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        final var dto = ProductDTO.fromProduct(product);
        final var requestJson = asJson(new ProductCreateRequest("Product",
                product.getPrice()));
        when(service.createProduct(any())).thenReturn(dto);
        // Action && Check
        final var resultActions = mockMvc.perform(post("/api/products")
                .contentType(APPLICATION_JSON)
                .content(requestJson));
        expectProduct(resultActions, HttpStatus.CREATED, product);
    }

    @ParameterizedTest
    @MethodSource("invalidArguments")
    void createProduct_whenInvalidRequest_shouldReturn400(String productName,
                                                          BigDecimal productPrice)
            throws Exception {
        // Given
        final var request = new ProductCreateRequest(productName, productPrice);
        // Action && Check
        final var resultActions = mockMvc.perform(post("/api/products")
                .contentType(APPLICATION_JSON)
                .content(asJson(request)));
        expectError(resultActions, HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateProduct_shouldReturn200AndDto() throws Exception {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        final var dto = ProductDTO.fromProduct(product);
        final var requestJson = asJson(new ProductUpdateRequest("Product",
                product.getPrice()));
        when(service.updateProduct(any(), any())).thenReturn(dto);
        // Action && Check
        final var resultActions = mockMvc.perform(put("/api/products/1")
                .contentType(APPLICATION_JSON)
                .content(requestJson));
        expectProduct(resultActions, HttpStatus.OK, product);
    }


    @ParameterizedTest
    @MethodSource("invalidArguments")
    void updateProduct_whenInvalidRequest_shouldReturn400(String productName,
                                                          BigDecimal productPrice)
            throws Exception {
        // Given
        final var request = new ProductUpdateRequest(productName, productPrice);
        // Action && Check
        final var resultActions = mockMvc.perform(put("/api/products/1")
                .contentType(APPLICATION_JSON)
                .content(asJson(request)));
        expectError(resultActions, HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("validNullArguments")
    void patchProduct_shouldReturn200AndDto(String productName,
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
                .contentType(APPLICATION_JSON)
                .content(requestJson));
        expectProduct(resultActions, HttpStatus.OK, product);
    }

    @Test
    void patchProduct_whenInvalidRequest_shouldReturn400()
            throws Exception {
        // Given
        final var request = new ProductPatchRequest("n", new BigDecimal("-1"));
        when(service.patchProduct(any(), any())).thenThrow(
                new ValidationException("Validation failed"));
        // Action && Check
        final var resultActions = mockMvc.perform(patch("/api/products/1")
                .contentType(APPLICATION_JSON)
                .content(asJson(request)));
        expectError(resultActions, HttpStatus.BAD_REQUEST);
    }

    @Test
    void getProductById_shouldReturn200AndDto() throws Exception {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        when(service.getProductById(1L)).thenReturn(ProductDTO.fromProduct(product));
        // Action && Check
        final var resultActions = mockMvc.perform(get("/api/products/1"));
        expectProduct(resultActions, HttpStatus.OK, product);
    }

    @Test
    void getProductById_whenProductNonExists_shouldReturn404() throws Exception {
        // Given
        when(service.getProductById(1L)).thenThrow(
                new EntityNotFoundException("Product not found"));
        // Action && Check
        final var resultActions = mockMvc.perform(get("/api/products/1"));
        expectError(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void getProducts_shouldReturn200AndDto() throws Exception {
        // Given
        final var products = List.of(
                TestProductFactory.createProduct(1L),
                TestProductFactory.createProduct(2L),
                TestProductFactory.createProduct(3L)
        );
        final var productNames = products
                .stream()
                .map(Product::getName)
                .toArray();
        final var productPrices = products
                .stream()
                .map(product -> product.getPrice().doubleValue())
                .toArray();
        final var request = PageRequest.of(0, 20);
        when(service.getProducts(any())).thenReturn(new ProductsDTO(products));
        // Action
        mockMvc.perform(get("/api/products")
                .contentType(APPLICATION_JSON)
                .content(asJson(request)))
                // Check
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(products.size()))
                .andExpect(jsonPath("$.count").value(products.size()))
                .andExpect(jsonPath("$.products[*].id", hasItems(1, 2, 3)))
                .andExpect(jsonPath("$.products[*].name", hasItems(productNames)))
                .andExpect(jsonPath("$.products[*].price", hasItems(productPrices)))
                .andExpect(jsonPath("$.products[*].createdAt").exists())
                .andExpect(jsonPath("$.products[*].createdAt",
                            everyItem(instanceOf(String.class))));

    }

    @Test
    void getProducts_whenProductsEmpty_shouldReturn200AndEmptyDto() throws Exception {
        // Given
        final var request = PageRequest.of(0, 20);
        when(service.getProducts(any())).thenReturn(new ProductsDTO(new ArrayList<>()));
        // Action
        mockMvc.perform(get("/api/products")
                .contentType(APPLICATION_JSON)
                .content(asJson(request)))
                // Check
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(0))
                .andExpect(jsonPath("$.count").value(0));

    }

    @Test
    void deleteProductById_shouldReturn204() throws Exception {
        // Action && Check
        mockMvc.perform(delete("/api/products/1"))
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

    void expectProduct(ResultActions actions,
                       HttpStatus expectedStatus,
                       Product expected) throws Exception {
        actions.andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.price").value(expected.getPrice()
                        .doubleValue()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isString());
    }

    void expectError(ResultActions actions, HttpStatus expectedStatus)
            throws Exception {
        actions.andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(expectedStatus.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
