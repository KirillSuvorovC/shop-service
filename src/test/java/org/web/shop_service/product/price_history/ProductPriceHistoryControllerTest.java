package org.web.shop_service.product.price_history;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.web.shop_service.support.configs.TestSecurityConfig;
import org.web.shop_service.product.support.TestProductFactory;
import org.web.shop_service.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductPriceHistoryController.class)
@ContextConfiguration(classes = TestSecurityConfig.class)
public class ProductPriceHistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductPriceHistoryService service;

    @Test
    @DisplayName("Должен успешно вернуть изменения цены и 200")
    void getPriceChanges_shouldReturn200AndDto() throws Exception {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        final var histories = List.of(
                TestProductFactory.createProductHistory(1L, product),
                TestProductFactory.createProductHistory(2L, product),
                TestProductFactory.createProductHistory(3L, product)
        );
        final var pageable = PageRequest.of(0,20);
        when(service.getPriceChanges(pageable))
                .thenReturn(new ProductsPriceHistoryDTO(histories));
        // Action
        final String json = JsonUtils.asJson(pageable);
        mockMvc.perform(get("/api/products/price_history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories").isArray())
                .andExpect(jsonPath("$.histories.length()").value(histories.size()))
                .andExpect(jsonPath("$.count").value(histories.size()))
                .andExpect(jsonPath("$.histories[*].id")
                        .value(hasItems(1, 2, 3)))
                .andExpect(jsonPath("$.histories[*].productId")
                        .value(everyItem(is(1))))
                .andExpect(jsonPath("$.histories[*].updatedAt")
                        .exists())
                .andExpect(jsonPath("$.histories[*].price")
                        .value(everyItem(is(50.00))));
    }

    @Test
    @DisplayName("Если изменений цен нет - вернуть 200 и пустое DTO")
    void getPriceChanges_whenProductsNotFound_shouldReturn200AndEmptyDto() throws Exception {
        // Given
        final var pageable = PageRequest.of(0,20);
        when(service.getPriceChanges(pageable))
                .thenReturn(new ProductsPriceHistoryDTO(new ArrayList<>()));
        // Action
        final String json = JsonUtils.asJson(pageable);
        mockMvc.perform(get("/api/products/price_history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories").isArray())
                .andExpect(jsonPath("$.histories.length()").value(0))
                .andExpect(jsonPath("$.count").value(0));
    }
}
