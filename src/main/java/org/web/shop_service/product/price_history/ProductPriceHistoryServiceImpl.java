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
        final var product = service.getProductEntityById(event.getProductId());
        var history = new ProductPriceHistory();
        history.setProduct(product);
        history.setUpdatedAt(event.getUpdatedAt());
        history.setPrice(event.getNewPrice());
        history = repository.save(history);
        log.info("Changed price for product with id {}", product.getId());
        return ProductPriceHistoryDTO.fromHistory(history);
    }

    @Override
    public ProductsPriceHistoryDTO getPriceChanges(Pageable pageable) {
        final var history = repository.findAll(pageable);
        return new ProductsPriceHistoryDTO(history.getContent());
    }
}
