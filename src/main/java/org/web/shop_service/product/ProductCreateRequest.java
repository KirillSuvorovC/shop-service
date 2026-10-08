package org.web.shop_service.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Запрос на создание продукта")
public record ProductCreateRequest(
        // TODO: добавить порог максимума символов
        @Size(min = MIN_SIZE_NAME, message = INVALID_SIZE_NAME)
        @NotBlank(message = INVALID_NOT_BLANK_NAME)
        @Schema(description = "Имя", example = "Product", minLength = MIN_SIZE_NAME)
        String name,
        // TODO: добавить порог максимальной цены
        @Positive(message = INVALID_DECIMAL_MIN_PRICE)
        @Schema(description = "Цена", example = "10.00", minLength = MIN_SIZE_NAME)
        BigDecimal price
) {
    public static final int MIN_SIZE_NAME = 3;
    public static final String INVALID_SIZE_NAME = "name should be 3 and more characters";
    public static final String INVALID_NOT_BLANK_NAME = "name should be not empty";
    public static final String INVALID_DECIMAL_MIN_PRICE = "the price must be greater than 0";
}
