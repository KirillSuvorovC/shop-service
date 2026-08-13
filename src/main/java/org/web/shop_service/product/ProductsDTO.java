package org.web.shop_service.product;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductsDTO {
    private final List<ProductDTO> products;
    private final int count;

    public ProductsDTO(List<Product> products) {
        this.products = products.stream()
                                .map(ProductDTO::fromProduct)
                                .collect(Collectors.toList());
        this.count = products.size();
    }
}
