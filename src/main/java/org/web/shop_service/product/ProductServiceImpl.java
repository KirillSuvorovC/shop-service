package org.web.shop_service.product;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web.shop_service.outbox.OutboxService;
import org.web.shop_service.product.events.ProductPriceChangedEvent;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQProperties;
import org.web.shop_service.utils.VersionHelper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    public static final String CACHE_NAME_PRODUCTS = "products";
    private final ProductRepository repository;
    private final ProductPatchValidator validator;
    private final ProductRabbitMQProperties productProperties;
    private final OutboxService outboxService;
    private final VersionHelper versionHelper;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateRequest request) {
        final var product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        product.setArchived(false);
        repository.save(product);
        publishEvent(product, product.getCreatedAt());
        log.info("Created Product with id: {}", product.getId());
        return ProductDTO.fromProduct(product);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME_PRODUCTS, key = "#id")
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        final var product = getProductEntityById(id);
        versionHelper.checkVersion(request.getVersion(), product.getVersion());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        repository.saveAndFlush(product);
        publishEvent(product, ZonedDateTime.now(ZoneOffset.UTC));
        log.info("Updated Product with id: {}", id);
        return ProductDTO.fromProduct(product);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME_PRODUCTS, key = "#id")
    public ProductDTO patchProduct(Long id, ProductPatchRequest request) {
        final var product = getProductEntityById(id);
        versionHelper.checkVersion(request.getVersion(), product.getVersion());
        validator.validate(request);
        if(request.getName() != null) product.setName(request.getName());
        if(request.getPrice() != null) product.setPrice(request.getPrice());
        repository.saveAndFlush(product);
        if(request.getPrice() != null)
            publishEvent(product, ZonedDateTime.now(ZoneOffset.UTC));

        log.info("Patched Product with id: {}", id);
        return ProductDTO.fromProduct(product);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME_PRODUCTS, key = "#id")
    public ProductDTO getProductById(Long id) {
        final var product = getProductEntityById(id);
        return ProductDTO.fromProduct(product);
    }

    @Override
    public ProductsDTO getProducts(Pageable pageable) {
        final var products = repository.findAll(pageable)
                .getContent();
        return new ProductsDTO(products);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME_PRODUCTS, key = "#id")
    public void deleteProduct(Long id) {
        repository.markProductAsArchived(id);
        log.info("Archived Product with id: {}", id);
    }

    @Override
    public Product getProductEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with id: " + id));
    }

    @Override
    public Map<Long, Product> getProductEntitiesAsMap(Iterable<Long> ids) {
        return repository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private void publishEvent(Product product, ZonedDateTime updatedAt) {
        final var event = new ProductPriceChangedEvent(product.getId(),
                product.getPrice(), updatedAt);
        outboxService.sendEvent(
                productProperties.getExchange(),
                productProperties.getRoutingKeyPriceChanged(),
                event);
    }
}
