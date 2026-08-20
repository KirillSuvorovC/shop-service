package org.web.shop_service.product.price_history;

import org.springframework.data.domain.Pageable;
import org.web.shop_service.product.Product;

public interface ProductPriceHistoryService {
    ProductPriceHistoryDTO saveChange(Product product);
    ProductsPriceHistoryDTO getPriceChanges(Pageable pageable);
}
