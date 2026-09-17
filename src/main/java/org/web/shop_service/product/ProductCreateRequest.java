package org.web.shop_service.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @Size(min = MIN_SIZE_NAME, message = INVALID_SIZE_NAME)
        @NotBlank(message = INVALID_NOT_BLANK_NAME)
        String name,
        @Positive(message = INVALID_DECIMAL_MIN_PRICE)
        BigDecimal price
) {
    public static final int MIN_SIZE_NAME = 3;
    public static final String INVALID_SIZE_NAME = "name should be 3 and more characters";
    public static final String INVALID_NOT_BLANK_NAME = "name should be not empty";
    public static final String INVALID_DECIMAL_MIN_PRICE = "the price must be greater than 0";
}
