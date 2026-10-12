package org.web.shop_service.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web.shop_service.utils.VersionedRequest;

import java.math.BigDecimal;

import static org.web.shop_service.product.ProductConstants.*;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на частичное обновление продукта")
public class ProductUpdateRequest extends VersionedRequest {
    @Size(min = MIN_SIZE_NAME,
          max = MAX_SIZE_NAME, message = INVALID_SIZE_NAME)
    @NotBlank(message = INVALID_NOT_BLANK_NAME)
    @Schema(description = "Имя", example = "Product",
            minLength = MIN_SIZE_NAME,
            maxLength = MAX_SIZE_NAME)
    private final String name;
    @Positive(message = INVALID_PRICE)
    @Digits(integer = PRECISION_PRICE,
            fraction = FRACTION_PRICE, message = INVALID_PRICE)
    @Schema(description = "Цена", example = "10.00",
            minimum = MIN_PRICE + "",
            maximum = MAX_PRICE + "")
    private final BigDecimal price;
}
