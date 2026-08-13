package org.web.shop_service.product;

import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO createProduct(ProductCreateRequest request);
    ProductDTO updateProduct(Long id, ProductUpdateRequest request);
    ProductDTO patchProduct(Long id, ProductPatchRequest request);
    ProductDTO getProductById(Long id);
    ProductsDTO getProducts(Pageable pageable);
    void deleteProduct(Long id);
}
