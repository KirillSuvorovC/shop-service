package org.web.shop_service.orders;

import lombok.Getter;

import java.util.List;

@Getter
public class OrdersDTO {
    private final List<OrderDTO> orders;
    private final int count;

    public OrdersDTO(List<Order> orders) {
        this.orders = orders.stream()
                .map(OrderDTO::new)
                .toList();
        this.count = this.orders.size();
    }
}
