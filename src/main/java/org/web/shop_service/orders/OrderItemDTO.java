package org.web.shop_service.orders;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Позиция заказа")
public record OrderItemDTO(
        @Schema(description = "ID позиции заказа", example = "1")
        Long id,
        @Schema(description = "ID продукта", example = "1")
        Long productId,
        @Schema(description = "Количество продукта", example = "1")
        Integer amount,
        @Schema(description = "Цена продукта на момент создания заказа", example = "10")
        BigDecimal priceAtOrder) {
    public OrderItemDTO(OrderItem orderItem) {
        this(orderItem.getId(),
            orderItem.getProduct().getId(),
            orderItem.getAmount(),
            orderItem.getPriceAtOrder());
    }
}
