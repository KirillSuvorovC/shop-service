package org.web.shop_service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.web.shop_service.configs.CaffeineCacheConfig;
import org.web.shop_service.outbox.OutboxService;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQProperties;
import org.web.shop_service.product.support.TestProductFactory;
import org.web.shop_service.utils.VersionHelper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {
        CaffeineCacheConfig.class,
        ProductServiceImpl.class,
        CaffeineCacheManager.class
})
@EnableCaching
public class ProductServiceImplCacheTest {
    @MockitoBean
    private ProductRepository repository;
    @MockitoBean
    private ProductPatchValidator validator;
    @MockitoBean
    private ProductRabbitMQProperties productProperties;
    @MockitoBean
    private OutboxService outboxService;
    @MockitoBean
    private VersionHelper versionHelper;

    @Autowired
    private ProductService service;
    @Autowired
    private CaffeineCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.removeCache(ProductServiceImpl.CACHE_NAME_PRODUCTS);
    }

    @Test
    @DisplayName("После первого GET-вызова вернуть закэшированный продукт")
    void getProductById_shouldReturnCacheProduct() {
        // Given
        final var product = TestProductFactory.createProduct(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        service.getProductById(1L);
        reset(repository);

        // Action
        final var cachedDto = service.getProductById(1L);

        // Check
        verify(repository, never()).findById(1L);
        assertThat(cachedDto.getName()).isEqualTo(product.getName());
    }

    @Test
    @DisplayName("После обновления вернуть актуальный закэшированный продукт")
    void patchProduct_shouldReturnActualCacheProduct() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(
                    TestProductFactory.createProduct(1L)));

        service.getProductById(1L);
        service.patchProduct(1L, new ProductPatchRequest("New Product", null));
        reset(repository);

        // Action
        final var newDto = service.getProductById(1L);

        // Check
        verify(repository, never()).findById(1L);
        assertThat(newDto.getName()).isEqualTo("New Product");
    }
    @Test
    @DisplayName("После удаления сбросить кэш удаленного продукта")
    void deleteProduct_shouldRemoveCacheProduct() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(
                    TestProductFactory.createProduct(1L)));
        service.getProductById(1L);
        reset(repository);

        // Action
        service.deleteProduct(1L);

        // Check
        assertThatThrownBy(() -> service.getProductById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
