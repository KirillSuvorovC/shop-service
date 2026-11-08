package org.web.shop_service.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.web.shop_service.utils.VersionedResponse;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Schema(description = "Заказ")
public class OrderDTO extends VersionedResponse {
    @Schema(description = "ID заказа", example = "1")
    private Long id;
    @Schema(description = "Статус заказа", example = "NEW")
    private OrderStatus status;
    @Schema(description = "Список позиций заказа")
    private List<OrderItemDTO> items;
    @Schema(description = "Количество позиций заказа", example = "1")
    private int count;
    @Schema(description = "Дата создания заказа")
    private ZonedDateTime createdAt;

    public OrderDTO() {}

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.items = order.getItems()
                .stream()
                .map(OrderItemDTO::new)
                .toList();
        this.count = this.items.size();
        setVersion(order.getVersion());
    }
}
