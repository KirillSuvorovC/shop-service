package org.web.shop_service.product.price_history;

import org.web.shop_service.product.Product;

public interface ProductPriceHistoryService {
    ProductPriceHistoryResponse saveChange(Product product);
}
