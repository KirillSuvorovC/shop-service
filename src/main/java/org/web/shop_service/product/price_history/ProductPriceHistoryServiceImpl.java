package org.web.shop_service.product.price_history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.web.shop_service.product.ProductService;
import org.web.shop_service.product.events.ProductPriceChangedEvent;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {
    private final ProductService service;
    private final ProductPriceHistoryRepository repository;

    @Override
    public ProductPriceHistoryDTO saveChange(ProductPriceChangedEvent event) {
        final var product = service.getProductEntityById(event.id());
        var history = new ProductPriceHistory();
        history.setProduct(product);
        history.setUpdatedAt(event.updatedAt());
        history.setPrice(event.newPrice());
        history = repository.save(history);
        return ProductPriceHistoryDTO.fromHistory(history);
    }

    @Override
    public ProductsPriceHistoryDTO getPriceChanges(Pageable pageable) {
        final var history = repository.findAll(pageable);
        return new ProductsPriceHistoryDTO(history.getContent());
    }
}
