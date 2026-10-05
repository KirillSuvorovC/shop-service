package org.web.shop_service.orders;

import org.web.shop_service.product.TestProductFactory;
import org.web.shop_service.user.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

class TestOrderFactory {
    /**
     * @param id идентификатор заказа
     * @param user пользователь, требующий заказ
     * @return {@link Order} заказ с одной позицией товара с привязкой к сущности
     */
    static Order order(Long id, User user) {
        final var order = new Order();
        order.setId(id);
        order.setUser(user);
        final var product = TestProductFactory.createProduct(1L);
        final var item = new OrderItem();
        item.setId(1L);
        item.setOrder(order);
        item.setProduct(product);
        item.setAmount(1);
        item.setPriceAtOrder(product.getPrice());
        order.setItems(List.of(item));
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        order.setVersion(1L);
        return order;
    }

    /**
     * @param id идентификатор заказа
     * @param user пользователь, требующий заказ
     * @return {@link Order} заказ без позиций товаров
     */
    static Order orderWithoutItems(Long id, User user) {
        final var order = new Order();
        order.setId(id);
        order.setUser(user);
        order.setItems(List.of());
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        order.setVersion(1L);
        return order;
    }

    /**
     * @return позицию товара без привязки к сущности {@link Order}
     */
    static OrderItem orderItem() {
        final var product = TestProductFactory.createProduct(1L);
        final var item = new OrderItem();
        item.setId(1L);
        item.setOrder(null);
        item.setProduct(product);
        item.setAmount(1);
        item.setPriceAtOrder(product.getPrice());
        return item;
    }
}
