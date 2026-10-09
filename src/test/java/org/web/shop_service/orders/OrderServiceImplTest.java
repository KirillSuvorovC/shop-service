package org.web.shop_service.orders;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.web.shop_service.orders.support.TestOrderFactory;
import org.web.shop_service.product.Product;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.product.support.TestProductFactory;
import org.web.shop_service.user.CurrentUser;
import org.web.shop_service.user.support.TestUserFactory;
import org.web.shop_service.user.User;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.VersionHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private static final User DEFAULT_USER = TestUserFactory.defaultUserEntity();
    private static final CurrentUser DEFAULT_AUTH_USER = TestUserFactory.defaultAuthUser();

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private VersionHelper versionHelper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Nested
    @DisplayName("Создание заказа")
    class CreateOrder {

        @Test
        @DisplayName("Создаёт заказ и возвращает DTO при валидных данных")
        void shouldCreateOrderAndReturnDto() {
            // Given
            final var request = createRequest();
            final Product[] products = {
                    TestProductFactory.createProduct(1L),
                    TestProductFactory.createProduct(2L)
            };

            when(userService.getUserById(DEFAULT_USER.getId())).thenReturn(DEFAULT_USER);
            when(productService.getProductEntityById(anyLong())).thenReturn(
                    products[0],
                    products[1]
            );
            // Action
            final var response = orderService.create(request, DEFAULT_AUTH_USER);
            // Check
            assertThat(response).isNotNull();
            verify(orderRepository, times(1)).save(any());
            verify(userService, times(1)).getUserById(DEFAULT_USER.getId());
            verify(productService, times(products.length)).getProductEntityById(anyLong());
        }

        @Test
        @DisplayName("Бросает исключение, если продукт не найден")
        void shouldThrowWhenProductNotFound() {
            // Given
            final var request = createRequest();
            when(userService.getUserById(DEFAULT_USER.getId())).thenReturn(DEFAULT_USER);
            when(productService.getProductEntityById(any())).thenThrow(EntityNotFoundException.class);
            // Action && Check
            assertThatThrownBy(() -> orderService.create(request, DEFAULT_AUTH_USER))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Бросает исключение, если пользователь не найден")
        void shouldThrowWhenUserNotFound() {
            // Given
            final var request = createRequest();
            when(userService.getUserById(DEFAULT_USER.getId()))
                            .thenThrow(EntityNotFoundException.class);
            // Action && Check
            assertThatThrownBy(() -> orderService.create(request, DEFAULT_AUTH_USER))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        private OrderCreateRequest createRequest() {
            return new OrderCreateRequest(new OrderCreateRequest.Item[]{
                    new OrderCreateRequest.Item(1L, 1),
                    new OrderCreateRequest.Item(2L, 2)
            });
        }
    }

    @Nested
    @DisplayName("Обновление статуса заказа")
    class UpdateStatus {

        @Test
        @DisplayName("Обновляет статус и возвращает DTO при валидных данных")
        void shouldUpdateStatusAndReturnDto() {
            // Given
            final var request = createRequest();
            final var order = TestOrderFactory.order(1L, DEFAULT_USER);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            // Action
            final var response = orderService.updateStatus(1L, request);
            // Check
            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualByComparingTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("Бросает исключение, если заказ не найден")
        void shouldThrowWhenOrderNotFound() {
            // Given
            when(orderRepository.findById(1L)).thenThrow(
                    new EntityNotFoundException("Order not found with id 1"));
            // Action && Check
            assertThatThrownBy(() -> orderService.updateStatus(1L, createRequest()));
        }

        @Test
        @DisplayName("Бросает OptimisticLockingFailureException при несовпадении версии")
        void shouldThrowWhenVersionMismatch() {
            // Given
            final var order = TestOrderFactory.order(1L, DEFAULT_USER);
            order.setVersion(2L);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            doThrow(new OptimisticLockingFailureException("Data is old, reload page."))
                    .when(versionHelper)
                    .checkVersion(anyLong(), anyLong());
            // Action && Check
            assertThatThrownBy(() -> orderService.updateStatus(1L, createRequest()));
            verify(orderRepository, times(1)).findById(1L);
        }

        private OrderUpdateStatusRequest createRequest() {
            final var request = new OrderUpdateStatusRequest(OrderStatus.DELIVERED.name());
            request.setVersion(1L);
            return request;
        }
    }

    @Nested
    @DisplayName("Поиск заказа по ID")
    class FindById {

        @Test
        @DisplayName("Возвращает DTO, если заказ найден")
        void shouldReturnDtoWhenFound() {
            // Given
            final var order = TestOrderFactory.order(1L, DEFAULT_USER);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            // Action
            orderService.findById(1L);
            // Check
            verify(orderRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Бросает исключение, если заказ не найден")
        void shouldThrowWhenNotFound() {
            // Given
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());
            // Action && Check
            assertThatThrownBy(() -> orderService.findById(1L))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(orderRepository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Поиск заказов пользователя")
    class FindAllByUserId {

        @Test
        @DisplayName("Возвращает страницу заказов пользователя")
        void shouldReturnPageOfOrders() {
            // Given
            final var orders = List.of(
                    TestOrderFactory.order(1L, DEFAULT_USER),
                    TestOrderFactory.order(2L, DEFAULT_USER)
            );
            final var pageable = PageRequest.of(0, 20);
            when(orderRepository.findAllByUserId(DEFAULT_AUTH_USER.getId(), pageable))
                    .thenReturn(orders);
            // Action
            final var ordersDto = orderService.findAllByUserId(DEFAULT_AUTH_USER, pageable);
            // Check
            verify(orderRepository, times(1))
                    .findAllByUserId(DEFAULT_AUTH_USER.getId(), pageable);
            assertThat(ordersDto.getOrders()).hasSize(orders.size());
            assertThat(ordersDto.getCount()).isEqualTo(orders.size());
        }

        @Test
        @DisplayName("Возвращает пустую страницу, если заказов нет")
        void shouldReturnEmptyPageWhenNoOrders() {
            // Given
            final var pageable = PageRequest.of(0, 20);
            when(orderRepository.findAllByUserId(DEFAULT_AUTH_USER.getId(), pageable))
                    .thenReturn(List.of());
            // Action
            final var ordersDto = orderService.findAllByUserId(DEFAULT_AUTH_USER, pageable);
            // Check
            verify(orderRepository, times(1))
                    .findAllByUserId(DEFAULT_AUTH_USER.getId(), pageable);
            assertThat(ordersDto.getOrders()).isEmpty();
            assertThat(ordersDto.getCount()).isEqualTo(0);
        }
    }
}
