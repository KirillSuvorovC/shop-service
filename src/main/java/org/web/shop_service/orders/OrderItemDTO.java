package org.web.shop_service.orders;

import java.math.BigDecimal;

public record OrderItemDTO(Long id,
                           Long productId,
                           Integer amount,
                           BigDecimal priceAtOrder) {
    public OrderItemDTO(OrderItem orderItem) {
        this(orderItem.getId(),
            orderItem.getProduct().getId(),
            orderItem.getAmount(),
            orderItem.getPriceAtOrder());
    }
}
