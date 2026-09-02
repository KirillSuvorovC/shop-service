package org.web.shop_service.product.price_history;

import org.springframework.data.domain.Pageable;
import org.web.shop_service.product.events.ProductPriceChangedEvent;

public interface ProductPriceHistoryService {
    ProductPriceHistoryDTO saveChange(ProductPriceChangedEvent event);
    ProductsPriceHistoryDTO getPriceChanges(Pageable pageable);
}
