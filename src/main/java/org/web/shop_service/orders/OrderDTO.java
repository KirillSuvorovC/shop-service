package org.web.shop_service.orders;

import lombok.Getter;
import org.web.shop_service.utils.VersionedResponse;

import java.util.List;

@Getter
public class OrderDTO extends VersionedResponse {
    private final Long id;
    private final OrderStatus status;
    private final List<OrderItemDTO> items;
    private final int count;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.items = order.getItems()
                .stream()
                .map(OrderItemDTO::new)
                .toList();
        this.count = this.items.size();
        setVersion(order.getVersion());
    }
}
