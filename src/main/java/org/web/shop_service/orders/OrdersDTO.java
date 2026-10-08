package org.web.shop_service.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "Список заказов")
public class OrdersDTO {
    private final List<OrderDTO> orders;
    @Schema(title = "Количество заказов", example = "1")
    private final int count;

    public OrdersDTO(List<Order> orders) {
        this.orders = orders.stream()
                .map(OrderDTO::new)
                .toList();
        this.count = this.orders.size();
    }
}
