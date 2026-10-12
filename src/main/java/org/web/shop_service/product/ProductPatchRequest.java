package org.web.shop_service.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.web.shop_service.utils.VersionedRequest;

import java.math.BigDecimal;

import static org.web.shop_service.product.ProductConstants.*;

@Getter
@Schema(description = "Запрос на частичное обновление продукта")
public class ProductPatchRequest extends VersionedRequest {
    @Schema(description = "Имя", example = "Product",
            minLength = MIN_SIZE_NAME,
            maxLength = MAX_SIZE_NAME)
    private final String name;
    @Schema(description = "Цена", example = "10.00", minLength = MIN_SIZE_NAME)
    private final BigDecimal price;

    public ProductPatchRequest(
            String name,
            BigDecimal price
    ) {
        this.name = name;
        this.price = price;
    }
}
