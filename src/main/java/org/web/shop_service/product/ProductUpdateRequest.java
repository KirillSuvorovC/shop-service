package org.web.shop_service.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.web.shop_service.utils.VersionedRequest;

import java.math.BigDecimal;

import static org.web.shop_service.product.ProductCreateRequest.*;

@Getter
@Schema(description = "Запрос на частичное обновление продукта")
public class ProductUpdateRequest extends VersionedRequest {
    @Size(min = MIN_SIZE_NAME, message = INVALID_SIZE_NAME)
    @NotBlank(message = INVALID_NOT_BLANK_NAME)
    @Schema(description = "Имя", example = "Product", minLength = MIN_SIZE_NAME)
    private final String name;
    @Positive(message = INVALID_DECIMAL_MIN_PRICE)
    @Schema(description = "Цена", example = "10.00", minLength = MIN_SIZE_NAME)
    private final BigDecimal price;

    public ProductUpdateRequest(
            @Size(min = MIN_SIZE_NAME, message = INVALID_SIZE_NAME)
            @NotBlank(message = INVALID_NOT_BLANK_NAME)
            String name,
            @Positive(message = INVALID_DECIMAL_MIN_PRICE)
            BigDecimal price
    ) {
        this.name = name;
        this.price = price;
    }
}
