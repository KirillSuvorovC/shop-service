package org.web.shop_service.product;

import lombok.Getter;
import org.web.shop_service.utils.VersionedResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

@Getter
public class ProductDTO extends VersionedResponse {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final ZonedDateTime createdAt;

    public ProductDTO(Long id, String name,
                      BigDecimal price, ZonedDateTime createdAt,
                      Long version) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.createdAt = createdAt;
        setVersion(version);
    }

    public static ProductDTO fromProduct(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice().setScale(2, RoundingMode.HALF_UP),
                product.getCreatedAt(),
                product.getVersion());
    }
}
