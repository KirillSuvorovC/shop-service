package org.web.shop_service.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.web.shop_service.configs.CaffeineCacheConfig;
import org.web.shop_service.orders.support.TestOrderFactory;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.support.TestUserDefaults;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.VersionHelper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {
        CaffeineCacheConfig.class,
        OrderServiceImpl.class,
        CaffeineCacheManager.class
})
@EnableCaching
public class OrderServiceImplCacheTest {
    @MockitoBean
    private OrderRepository repository;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private VersionHelper versionHelper;

    @Autowired
    private OrderService service;
    @Autowired
    private CaffeineCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.removeCache(OrderServiceImpl.CACHE_NAME_ORDERS);
    }

    @Test
    @DisplayName("После первого GET-вызова вернуть закэшированный заказ")
    void findById_shouldReturnCacheOrder() {
        // Given
        final var order = TestOrderFactory.order(1L, TestUserDefaults.ENTITY_USER);
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        service.findById(1L);
        reset(repository);

        // Action
        final var cachedDto = service.findById(1L);

        // Check
        verify(repository, never()).findById(1L);
        assertThat(cachedDto.getStatus()).isEqualTo(order.getStatus());
    }

    @Test
    @DisplayName("После обновления вернуть актуальный закэшированный заказ")
    void updateStatus_shouldReturnActualCacheOrder() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(
                TestOrderFactory.order(1L, TestUserDefaults.ENTITY_USER)));

        service.findById(1L);
        service.updateStatus(1L, new OrderUpdateStatusRequest(
                                    OrderStatus.CANCELLED.name()));
        reset(repository);

        // Action
        final var newDto = service.findById(1L);

        // Check
        verify(repository, never()).findById(1L);
        assertThat(newDto.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
