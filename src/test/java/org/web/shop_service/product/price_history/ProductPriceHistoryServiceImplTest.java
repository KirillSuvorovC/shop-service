package org.web.shop_service.product.price_history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web.shop_service.product.Product;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductPriceHistoryServiceImplTest {
    @Mock
    private ProductPriceHistoryRepository repository;
    @InjectMocks
    private ProductPriceHistoryServiceImpl service;
    @Test
    void saveChange_shouldReturnDto() {
        // Given
        final var product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        product.setPrice(new BigDecimal("50.00"));

        final var history = new ProductPriceHistory();
        history.setId(1L);
        history.setProduct(product);
        ZonedDateTime updated = ZonedDateTime.now(ZoneOffset.UTC).minusDays(5);
        history.setUpdatedAt(updated);
        history.setPrice(new BigDecimal("50.00"));
        when(repository.save(any(ProductPriceHistory.class))).thenReturn(history);
        // Action
        final ProductPriceHistoryResponse response = service.saveChange(product);
        // Check
        assertNotNull(response);
        assertEquals(history.getId(), response.id());
        assertEquals(product.getId(), response.productId());
        assertEquals(history.getPrice(), response.price());
        assertEquals(history.getUpdatedAt(), response.updatedAt());
        verify(repository, times(1)).save(any());
    }
}
