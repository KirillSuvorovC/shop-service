package org.web.shop_service.product.price_history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web.shop_service.product.Product;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {
    private final ProductPriceHistoryRepository repository;

    @Override
    public ProductPriceHistoryResponse saveChange(Product product) {
        var history = new ProductPriceHistory();
        history.setProduct(product);
        history.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        history.setPrice(product.getPrice());
        history = repository.save(history);
        return ProductPriceHistoryResponse.fromHistory(history);
    }
}
