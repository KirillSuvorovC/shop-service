package org.web.shop_service.product.price_history;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "Список изменений цен")
public class ProductsPriceHistoryDTO {
    private final List<ProductPriceHistoryDTO> histories;
    @Schema(description = "Количество изменений", example = "1")
    private final int count;

    public ProductsPriceHistoryDTO(List<ProductPriceHistory> products) {
        this.histories = products.stream()
                .map(ProductPriceHistoryDTO::fromHistory)
                .toList();
        count = products.size();
    }
}
