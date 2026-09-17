package org.web.shop_service.product.price_history;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.product.TestProductFactory;
import org.web.shop_service.product.events.ProductPriceChangedEvent;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductPriceHistoryServiceImplTest {
    @Mock
    private ProductPriceHistoryRepository repository;
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductPriceHistoryServiceImpl service;
    @Test
    @DisplayName("После успешного сохранения изменения цены вернуть DTO")
    void saveChange_shouldReturnDto() {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        final var history = new ProductPriceHistory();
        history.setId(1L);
        history.setProduct(product);
        final var updated = ZonedDateTime.now(ZoneOffset.UTC).minusDays(5);
        history.setUpdatedAt(updated);
        history.setPrice(product.getPrice());
        when(repository.save(any(ProductPriceHistory.class))).thenReturn(history);
        when(productService.getProductEntityById(1L)).thenReturn(product);
        // Action
        final ProductPriceHistoryDTO response = service.saveChange(
            new ProductPriceChangedEvent(product.getId(), product.getPrice(), updated));
        // Check
        assertNotNull(response);
        assertEquals(history.getId(), response.id());
        assertEquals(product.getId(), response.productId());
        assertEquals(history.getPrice(), response.price());
        assertEquals(history.getUpdatedAt(), response.updatedAt());
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Должен вернуть список изменений цен")
    void getPriceChanges_shouldReturnDto() {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        final var histories = List.of(
                    TestProductFactory.createProductHistory(1L, product),
                    TestProductFactory.createProductHistory(2L, product),
                    TestProductFactory.createProductHistory(3L, product));
        final var pageable = PageRequest.of(0, 20);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(histories));
        // Action
        final ProductsPriceHistoryDTO dto = service.getPriceChanges(pageable);
        final int count = dto.getCount();
        // Check
        assertNotNull(dto);
        assertEquals(histories.size(), count);
        verify(repository, times(1)).findAll(pageable);
        for (int i = 0; i < count; i ++) {
            final var entity = histories.get(i);
            final var response = dto.getHistories().get(i);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getProduct().getId(), response.productId());
            assertEquals(entity.getPrice(), response.price());
            assertEquals(entity.getUpdatedAt(), response.updatedAt());
        }
    }

    @Test
    @DisplayName("Если пустой список изменений цен - вернуть пустое DTO")
    void getPriceChanges_shouldReturnEmptyDto() {
        // Given
        final var histories = new ArrayList<ProductPriceHistory>();
        final var pageable = PageRequest.of(0, 20);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(histories));
        // Action
        final ProductsPriceHistoryDTO dto = service.getPriceChanges(pageable);
        final int count = dto.getCount();
        // Check
        assertNotNull(dto);
        assertEquals(0, dto.getCount());
        assertTrue(dto.getHistories().isEmpty());
        verify(repository, times(1)).findAll(pageable);
    }
}
