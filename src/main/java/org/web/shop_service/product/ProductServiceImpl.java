package org.web.shop_service.product;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.web.shop_service.utils.exceptions.EntityNotFoundException;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductPatchValidator validator;
    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        final var product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        repository.save(product);
        return ProductDTO.fromProduct(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        final var product = findById(id);
        product.setName(request.name());
        product.setPrice(request.price());
        repository.save(product);
        return ProductDTO.fromProduct(product);
    }

    @Override
    public ProductDTO patchProduct(Long id, ProductPatchRequest request) {
        final var product = findById(id);
        validator.validate(request);
        if(request.name() != null) product.setName(request.name());
        if(request.price() != null) product.setPrice(request.price());
        repository.save(product);
        return ProductDTO.fromProduct(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        final var product = findById(id);
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

    private Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class));
    }
}
