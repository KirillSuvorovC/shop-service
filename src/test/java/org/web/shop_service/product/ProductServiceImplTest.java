package org.web.shop_service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.web.shop_service.outbox.OutboxService;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQProperties;
import org.web.shop_service.product.support.TestProductFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Метод {@link ProductServiceImpl#deleteProduct(Long)} не тестируется.
 * Причина: простая делегация {@link ProductRepository#markProductAsArchived(Long)}
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository repository;
    @Mock
    private ProductPatchValidator validator;
    @Mock
    private ProductRabbitMQProperties properties;
    @Mock
    private OutboxService outboxService;
    @InjectMocks
    private ProductServiceImpl productService;
    @Test
    @DisplayName("При корректном запросе - создает продукт и возвращает DTO")
    void createProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductCreateRequest(name, price);
        // Action
        final var response = productService.createProduct(request);

        // Check
        assertNotNull(response);
        assertEquals(name, response.getName());
        assertEquals(0, response.getPrice().compareTo(price));
        verify(repository, times(1)).save(any());
        verify(outboxService, times(1))
                            .sendEvent(any(), any(), any());
    }

    @Test
    @DisplayName("Должен полностью обновить продукт и вернуть DTO")
    void updateProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductUpdateRequest(name, price);
        final var product = new Product();
        product.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        // Action
        final var response = productService.updateProduct(1L, request);

        // Check
        assertNotNull(response);
        assertEquals(name, response.getName());
        assertEquals(0, response.getPrice().compareTo(price));
        verify(repository, times(1)).saveAndFlush(product);
        verify(outboxService, times(1))
                .sendEvent(any(), any(), any());
    }
    @Test
    @DisplayName("Должен бросить исключение, если продукт не найден")
    void updateProduct_shouldThrowException_whenProductNotFound() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductUpdateRequest(name, price);
        final var product = new Product();
        product.setId(1L);
        when(repository.findById(1L)).thenThrow(new EntityNotFoundException(
                                                        "Product not found"));
        // Action
        assertThrows(EntityNotFoundException.class,
                () -> productService.updateProduct(1L, request));
    }

    @Test
    @DisplayName("Должен частично обновить продукт")
    void patchProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final var request = new ProductPatchRequest(null, price);
        final var product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setPrice(price);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        // Action
        final var response = productService.patchProduct(1L, request);

        // Check
        assertNotNull(response);
        assertEquals(product.getName(), response.getName());
        assertEquals(0, response.getPrice().compareTo(price));
        verify(validator, times(1)).validate(request);
        verify(repository, times(1)).saveAndFlush(product);
        verify(outboxService, times(1))
                .sendEvent(any(), any(), any());
    }

    @Test
    @DisplayName("Бросает исключение, если продукт не найден")
    void patchProduct_whenProductNotFound_throwException() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductPatchRequest(name, price);
        when(repository.findById(1L)).thenThrow(new EntityNotFoundException(
                                                        "Product not found"));
        // Action && Check
        assertThrows(EntityNotFoundException.class,
                () -> productService.patchProduct(1L, request));
    }

    @Test
    @DisplayName("Должен вернуть продукт в виде DTO")
    void getProductById_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var product = new Product();
        product.setId(1L);
        product.setName(name);
        product.setPrice(price);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        // Action
        final var response = productService.getProductById(1L);

        // Check
        assertNotNull(response);
        assertEquals(name, response.getName());
        assertEquals(0, response.getPrice().compareTo(price));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Если продукт не найден - кидает исключение")
    void getProductById_whenProductNotFound_throwException() {
        // Given
        when(repository.findById(1L)).thenThrow(EntityNotFoundException.class);
        // Action && Check
        assertThrows(EntityNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    @Test
    @DisplayName("Должен вернуть продукты в виде DTO")
    void getProducts_shouldReturnProductsDTO() {
        // Given
        final var products = List.of(
                TestProductFactory.createProduct(1L),
                TestProductFactory.createProduct(2L),
                TestProductFactory.createProduct(3L));
        final var page = new PageImpl<>(products);
        when(repository.findAll((Pageable)any())).thenReturn(page);
        // Action
        final var response = productService.getProducts(any());
        // Check
        assertNotNull(response);
        verify(repository, times(1)).findAll((Pageable)any());
        assertEquals(products.size(), response.getCount());
        for (int i = 0; i < products.size(); i++) {
            final var excepted = products.get(i);
            final var actual = response.getProducts().get(i);
            assertEquals(excepted.getName(), actual.getName());
            assertEquals(0, actual.getPrice().compareTo(excepted.getPrice()));
        }
    }
    @Test
    @DisplayName("Должен вернуть пустой DTO, если нет продуктов")
    void getProducts_shouldReturnEmptyDTO_whenProductsNotFound() {
        // Given
        when(repository.findAll((Pageable)any())).thenReturn(
                            new PageImpl<>(new ArrayList<>()));
        // Action
        final var response = productService.getProducts(any());
        // Check
        assertNotNull(response);
        assertTrue(response.getProducts().isEmpty());
        assertEquals(0, response.getCount());
        verify(repository, times(1)).findAll((Pageable)any());
    }
}