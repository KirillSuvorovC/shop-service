package org.web.shop_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.web.shop_service.product.ProductController;
import org.web.shop_service.product.ProductCreateRequest;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.support.configs.TestSecurityAOPConfig;
import org.web.shop_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(TestSecurityAOPConfig.class)
class GlobalRequestBodyHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService service;

    @Test
    @DisplayName("Вернуть 412, если HTTP-заголовок If-Match отсутствует")
    void shouldReturn412IfHeaderIfMatchIsNotFound() throws Exception {
        // Given
        final var request = new ProductCreateRequest(
                "Product", new BigDecimal("50.00"));

        // Action
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJson(request))
                        .with(csrf()))
                // Check
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.status").value(412));

    }

    @ParameterizedTest
    @MethodSource("invalidHttpHeadersIfMatch")
    @DisplayName("Вернуть 412, если HTTP-заголовок If-Match пустой")
    void shouldReturn412IfHeaderIfMatchIsEmpty(String emptyHeader) throws Exception {
        // Given
        final var request = new ProductCreateRequest(
                "Product", new BigDecimal("50.00"));

        // Action
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJson(request))
                        .with(csrf())
                        .header(HttpHeaders.IF_MATCH, emptyHeader))
                // Check
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.status").value(412));

    }

    static Stream<Arguments> invalidHttpHeadersIfMatch() {
        return Stream.of(
                arguments(""),
                arguments(" ")
        );
    }
}
