package org.web.shop_service.product.price_history;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Schema(description = "Изменение цены")
public record ProductPriceHistoryDTO(
        @Schema(description = "ID изменения", example = "1")
        Long id,
        @Schema(description = "ID продукта", example = "1")
        Long productId,
        @Schema(description = "Дата изменения цены", example = "2007-12-03T10:15:30+01:00")
        ZonedDateTime updatedAt,
        @Schema(description = "Цена", example = "10.00")
        BigDecimal price) {
    public static ProductPriceHistoryDTO fromHistory(ProductPriceHistory history) {
        return new ProductPriceHistoryDTO(
                history.getId(),
                history.getProduct().getId(),
                history.getUpdatedAt(),
                history.getPrice());
    }
}
