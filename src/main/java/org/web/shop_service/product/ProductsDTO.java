package org.web.shop_service.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Schema(description = "Список продуктов")
public class ProductsDTO {
    private final List<ProductDTO> products;
    @Schema(description = "Количество", example = "1")
    private final int count;

    public ProductsDTO(List<Product> products) {
        this.products = products.stream()
                                .map(ProductDTO::fromProduct)
                                .collect(Collectors.toList());
        this.count = products.size();
    }
}
