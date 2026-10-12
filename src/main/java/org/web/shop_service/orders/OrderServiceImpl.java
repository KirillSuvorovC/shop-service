package org.web.shop_service.orders;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web.shop_service.product.Product;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.user.User;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.VersionHelper;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final VersionHelper versionHelper;

    @Transactional
    public OrderDTO create(OrderCreateRequest request, AuthUser authUser) {
        final var user = userService.getUserById(authUser.getId());
        final var savedItems = new ArrayList<OrderItem>(request.items().size());
        final var order = new Order();

        final var expectedIds = request.items()
                .stream()
                .map(OrderCreateRequest.Item::productId)
                .toList();
        final var actualProducts = productService.getProductEntitiesAsMap(expectedIds);
        checkExistsProducts(expectedIds, actualProducts);
        for (int i = 0; i < expectedIds.size(); i++) {
            final Long expectedId = expectedIds.get(i);
            final var product = actualProducts.get(expectedId);
            final int amount = request.items().get(i).amount();

            final var savedItem = new OrderItem();
            savedItem.setOrder(order);
            savedItem.setProduct(product);
            savedItem.setAmount(amount);
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
    public OrdersDTO findAllByUserId(AuthUser user, Pageable pageable) {
        final var orders = orderRepository.findAllByUserId(user.getId(), pageable);
        return new OrdersDTO(orders);
    }

    private Order getOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                            "Order not found with id " + id));
    }



    private void checkExistsProducts(List<Long> expectedIds,
                                     Map<Long, Product> actualProducts) {
        Set<Long> nonExistsIds = null;
        for (final Long expectedId : expectedIds) {
            if(!actualProducts.containsKey(expectedId)) {
                if(nonExistsIds == null) nonExistsIds = new HashSet<>();
                nonExistsIds.add(expectedId);
            }
        }
        if(nonExistsIds != null) {
            throw new EntityNotFoundException("Products not found with ids: "
                    + nonExistsIds);
        }
    }
}
