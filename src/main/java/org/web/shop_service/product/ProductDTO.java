package org.web.shop_service.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.web.shop_service.utils.VersionedResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

@Getter
@Schema(description = "Продукт")
public class ProductDTO extends VersionedResponse {
    @Schema(description = "ID продукта", example = "1")
    private final Long id;
    @Schema(description = "Имя", example = "Product")
    private final String name;
    @Schema(description = "Цена", example = "10.00")
    private final BigDecimal price;
    @Schema(description = "Дата создания", example = "2007-12-03T10:15:30+01:00")
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
