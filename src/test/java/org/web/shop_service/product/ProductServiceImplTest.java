package org.web.shop_service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.web.shop_service.outbox.OutboxService;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQProperties;
import org.web.shop_service.product.support.TestProductFactory;
import org.web.shop_service.utils.VersionHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Метод {@link ProductServiceImpl#deleteProduct(Long)} не тестируется.
 * Причина: простая делегация {@link ProductRepository#markProductAsArchived(Long)}
 * Метод {@link ProductServiceImpl#getProductEntityById(Long)} (Long)}
 * не тестируется, потому что он был протестирован в разделе
 * {@link GetProductById}
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
    @Mock
    private VersionHelper versionHelper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("Создает продукт и возвращает DTO при корректном запросе")
    void createProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductCreateRequest(name, price);
        // Action
        final var response = productService.createProduct(request);

        // Check
        assertThat(response).isNotNull();
        assertThat(name).isEqualTo(response.getName());
        assertThat(response.getPrice()).isEqualByComparingTo(price);
        verify(repository, times(1)).save(any());
        verify(outboxService, times(1))
                            .sendEvent(any(), any(), any());
    }

    @Nested
    @DisplayName("Полное обновление продукта")
    class UpdateProduct {
        @Test
        @DisplayName("Должен полностью обновить продукт и вернуть DTO")
        void shouldReturnProductDTO() {
            // Given
            final var request = createRequest();
            final var product = TestProductFactory.createProduct(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(product));
            // Action
            final var response = productService.updateProduct(1L, request);

            // Check
            assertThat(response).isNotNull();
            assertThat(request.getName()).isEqualTo(response.getName());
            assertThat(request.getPrice()).isEqualByComparingTo(response.getPrice());
            verify(versionHelper, times(1))
                    .checkVersion(any(), any());
            verify(repository, times(1)).saveAndFlush(product);
            verify(outboxService, times(1))
                    .sendEvent(any(), any(), any());
        }
        @Test
        @DisplayName("Должен бросить исключение, если продукт не найден")
        void shouldThrowException_whenProductNotFound() {
            // Given
            when(repository.findById(1L)).thenThrow(new EntityNotFoundException(
                                                        "Product not found"));
            // Action
            assertThatThrownBy(() ->
                    productService.updateProduct(1L, createRequest()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Должен бросить исключение, если версии не совпадают")
        void shouldThrowException_whenVersionsNotMatch() {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            product.setVersion(1000L);
            when(repository.findById(1L)).thenReturn(Optional.of(product));
            doThrow(OptimisticLockingFailureException.class)
                    .when(versionHelper).checkVersion(any(), any());
            // Action
            assertThatThrownBy(() ->
                    productService.updateProduct(1L, createRequest()))
                    .isInstanceOf(OptimisticLockingFailureException.class);
        }

        private ProductUpdateRequest createRequest() {
            final var request = new ProductUpdateRequest(
                    "Product", new BigDecimal("50.00"));
            request.setVersion(1L);
            return request;
        }
    }

    @Nested
    @DisplayName("Частичное обновление продукта")
    class PatchProduct {
        @Test
        @DisplayName("Должен частично обновить продукт")
        void shouldReturnProductDTO() {
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
            assertThat(response).isNotNull();
            assertThat(product.getName()).isEqualTo(response.getName());
            assertThat(response.getPrice()).isEqualByComparingTo(price);
            verify(versionHelper, times(1))
                    .checkVersion(any(), any());
            verify(validator, times(1)).validate(request);
            verify(repository, times(1)).saveAndFlush(product);
            verify(outboxService, times(1))
                    .sendEvent(any(), any(), any());
        }

        @Test
        @DisplayName("Бросает исключение, если продукт не найден")
        void whenProductNotFound_throwException() {
            // Given
            when(repository.findById(1L))
                    .thenThrow(new EntityNotFoundException("Product not found"));
            // Action && Check
            assertThatThrownBy(() -> productService.patchProduct(1L, createRequest()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Должен бросить исключение, если версии не совпадают")
        void shouldThrowException_whenVersionsNotMatch() {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            product.setVersion(1000L);
            when(repository.findById(1L)).thenReturn(Optional.of(product));
            doThrow(OptimisticLockingFailureException.class)
                    .when(versionHelper).checkVersion(any(), any());
            // Action && Check
            assertThatThrownBy(() -> productService.patchProduct(1L, createRequest()))
                    .isInstanceOf(OptimisticLockingFailureException.class);
        }

        private ProductPatchRequest createRequest() {
            final var request = new ProductPatchRequest(
                    "Product", new BigDecimal("50.00"));
            request.setVersion(1L);
            return request;
        }
    }

    @Nested
    @DisplayName("Получение конкретного продукта по ID")
    class GetProductById {
        @Test
        @DisplayName("Должен вернуть продукт в виде DTO")
        void shouldReturnProductDTO() {
            // Given
            final var product = TestProductFactory.createProduct(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(product));
            // Action
            final var response = productService.getProductById(1L);

            // Check
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(product.getName());
            assertThat(response.getPrice()).isEqualByComparingTo(product.getPrice());
            verify(repository, times(1)).findById(1L);
        }
        @Test
        @DisplayName("Если продукт не найден - кидает исключение")
        void whenProductNotFound_throwException() {
            // Given
            when(repository.findById(1L)).thenThrow(EntityNotFoundException.class);
            // Action && Check
            assertThatThrownBy(() -> productService.getProductById(1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Получение продуктов")
    class GetProducts {
        @Test
        @DisplayName("Должен вернуть продукты в виде DTO")
        void shouldReturnProductsDTO() {
            // Given
            final var products = List.of(
                    TestProductFactory.createProduct(1L),
                    TestProductFactory.createProduct(2L),
                    TestProductFactory.createProduct(3L));
            when(repository.findAll((Pageable) any()))
                    .thenReturn(new PageImpl<>(products));
            // Action
            final var response = productService.getProducts(any());
            // Check
            assertThat(response).isNotNull();
            assertThat(response.getCount()).isEqualTo(products.size());
            verify(repository, times(1)).findAll((Pageable) any());
        }

        @Test
        @DisplayName("Должен вернуть пустой DTO, если нет продуктов")
        void shouldReturnEmptyDTO_whenProductsNotFound() {
            // Given
            when(repository.findAll((Pageable) any())).thenReturn(
                    new PageImpl<>(new ArrayList<>()));
            // Action
            final var response = productService.getProducts(any());
            // Check
            assertThat(response).isNotNull();
            assertThat(response.getProducts()).isEmpty();
            assertThat(response.getCount()).isEqualTo(0);
            verify(repository, times(1)).findAll((Pageable) any());
        }
    }
}