package org.web.shop_service.orders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.user.CurrentUser;
import org.web.shop_service.user.User;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.VersionHelper;
import org.web.shop_service.utils.exceptions.EntityNotFoundException;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final VersionHelper versionHelper;

    /**
     * Создает заказ в БД исходя из параметра {@code request}.
     * Заказ оформляется для {@link User}, определение пользователя
     * осуществляется по полю {@code currentUser.id}.
     * После создания заказ имеет статус {@code OrderStatus.NEW}.
     * @param request данные для создания заказа
     * @param currentUser текущий аутентифицированный пользователь
     * @see OrderStatus
     * @see OrderCreateRequest
     * @throws EntityNotFoundException если продукт или пользователь не найден
     * @return созданный заказ в виде {@link OrderDTO}
     */
    @Transactional
    public OrderDTO create(OrderCreateRequest request, CurrentUser currentUser) {
        final var user = userService.getUserById(currentUser.getId());
        final var requestItems = request.items();
        final var savedItems = new ArrayList<OrderItem>(requestItems.length);
        final var order = new Order();
        for(final var orderItem : requestItems) {
            final var product = productService.getProductEntityById(orderItem.productId());
            final var savedItem = new OrderItem();
            savedItem.setOrder(order);
            savedItem.setProduct(product);
            savedItem.setAmount(orderItem.amount());
            savedItem.setPriceAtOrder(product.getPrice());
            savedItems.add(savedItem);
        }
        order.setUser(user);
        order.setItems(savedItems);
        order.setStatus(OrderStatus.NEW);

        orderRepository.save(order);
        log.info("Created order for User with id: {}", user.getId());
        return new OrderDTO(order);
    }

    /**
     * @param id идентификатор заказа
     * @param request данные для изменения статуса
     * @throws EntityNotFoundException если заказ не найден
     * @throws OptimisticLockingFailureException если данные заказа устарели
     * @return обновленный заказ в виде {@link OrderDTO}
     */
    @Override
    public OrderDTO updateStatus(Long id, OrderUpdateStatusRequest request) {
        final var order = getOrderEntityById(id);
        versionHelper.checkVersion(request.getVersion(), order.getVersion());
        order.setStatus(OrderStatus.valueOf(request.getStatus()));

        orderRepository.saveAndFlush(order);
        log.info("Updated status of order for User with id: {}", order.getUser().getId());
        return new OrderDTO(order);
    }

    @Override
    public OrderDTO findById(Long id) {
        final Order order = getOrderEntityById(id);
        return new OrderDTO(order);
    }

    @Override
    public OrdersDTO findAllByUserId(CurrentUser user, Pageable pageable) {
        final var orders = orderRepository.findAllByUserId(user.getId(), pageable)
                                          .getContent();
        return new OrdersDTO(orders);
    }

    private Order getOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
    }
}
