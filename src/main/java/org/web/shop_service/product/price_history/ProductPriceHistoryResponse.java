package org.web.shop_service.product.price_history;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ProductPriceHistoryResponse(
        Long id,
        Long productId,
        ZonedDateTime updatedAt,
        BigDecimal price) {
    public static ProductPriceHistoryResponse fromHistory(ProductPriceHistory history) {
        return new ProductPriceHistoryResponse(
                history.getId(),
                history.getProduct().getId(),
                history.getUpdatedAt(),
                history.getPrice());
    }
}
