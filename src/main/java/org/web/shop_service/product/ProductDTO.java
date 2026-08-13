package org.web.shop_service.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

public record ProductDTO(Long id, String name,
                         BigDecimal price, ZonedDateTime createdAt) {
    public static ProductDTO fromProduct(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice().setScale(2, RoundingMode.HALF_UP),
                product.getCreatedAt());
    }
}
