package org.web.shop_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.web.shop_service.product.*;
import org.web.shop_service.product.support.TestProductFactory;
import org.web.shop_service.support.configs.TestSecurityAOPConfig;
import org.web.shop_service.support.TestUserDefaults;
import org.web.shop_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(TestSecurityAOPConfig.class)
class GlobalResponseBodyHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService service;

    @Test
    @DisplayName("""
            Вернуть 200 и HTTP-заголовок eTag, если передан
            непустой HTTP-заголовок If-Match и полученный объект
            класса наследует VersionedResponse
            """)
    void shouldReturn200IfHTTPHeaderIfMatchIsValidAndReturnETag() throws Exception {
        // Given
        final var request = new ProductCreateRequest(
                "Product", new BigDecimal("50.00"));
        final var product = TestProductFactory.createProduct(1L);
        when(service.updateProduct(any(), any())).thenReturn(ProductDTO.fromProduct(product));
        // Action
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJson(request))
                        .with(csrf())
                        .with(user(TestUserDefaults.CURRENT_ADMIN))
                        .header(HttpHeaders.IF_MATCH, "1"))
                // Check
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""));

    }

    @Test
    @DisplayName("""
            Вернуть 200 без HTTP-заголовока eTag, если полученный объект
            класса не наследует VersionedResponse
            """)
    void shouldReturn200WithoutETag() throws Exception {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        when(service.getProducts(any())).thenReturn(new ProductsDTO(List.of(product)));
        // Action
        mockMvc.perform(get("/api/products"))
                // Check
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HttpHeaders.ETAG));

    }
}
