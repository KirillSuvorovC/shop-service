package org.web.shop_service.product.price_history;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductsPriceHistoryDTO {
    private final List<ProductPriceHistoryDTO> histories;
    private final int count;

    public ProductsPriceHistoryDTO(List<ProductPriceHistory> products) {
        this.histories = products.stream()
                .map(ProductPriceHistoryDTO::fromHistory)
                .toList();
        count = products.size();
    }
}
