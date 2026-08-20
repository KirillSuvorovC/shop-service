package org.web.shop_service.product.price_history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.web.shop_service.product.Product;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {
    private final ProductPriceHistoryRepository repository;

    @Override
    public ProductPriceHistoryDTO saveChange(Product product) {
        var history = new ProductPriceHistory();
        history.setProduct(product);
        history.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        history.setPrice(product.getPrice());
        history = repository.save(history);
        return ProductPriceHistoryDTO.fromHistory(history);
    }

    @Override
    public ProductsPriceHistoryDTO getPriceChanges(Pageable pageable) {
        final var history = repository.findAll(pageable);
        return new ProductsPriceHistoryDTO(history.getContent());
    }
}
