package org.web.shop_service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.web.shop_service.product.events.ProductPriceChangedEvent;
import org.web.shop_service.product.rabbitmq.ProductRabbitMQProperties;
import org.web.shop_service.utils.exceptions.EntityNotFoundException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductPatchValidator validator;
    private final ProductRabbitMQProperties productProperties;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        final var product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        repository.save(product);
        publishEvent(product, product.getCreatedAt());
        return ProductDTO.fromProduct(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        final var product = getProductEntityById(id);
        product.setName(request.name());
        product.setPrice(request.price());
        repository.save(product);
        publishEvent(product, ZonedDateTime.now(ZoneOffset.UTC));
        return ProductDTO.fromProduct(product);
    }

    @Override
    public ProductDTO patchProduct(Long id, ProductPatchRequest request) {
        final var product = getProductEntityById(id);
        validator.validate(request);
        if(request.name() != null) product.setName(request.name());
        if(request.price() != null) product.setPrice(request.price());
        repository.save(product);
        if(request.price() != null)
            publishEvent(product, ZonedDateTime.now(ZoneOffset.UTC));

        return ProductDTO.fromProduct(product);
    }

    @Override
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
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Product getProductEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class));
    }

    private void publishEvent(Product product, ZonedDateTime updatedAt) {
        final var event = new ProductPriceChangedEvent(product.getId(),
                                        product.getPrice(), updatedAt);
        // TODO: позже применить паттерн Transactional OutBox
        //  для гарантии доставки сообщения
        rabbitTemplate.convertAndSend(
                productProperties.getExchange(),
                productProperties.getRoutingKeyPriceChanged(),
                event);
    }
}
