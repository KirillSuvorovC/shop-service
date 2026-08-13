package org.web.shop_service.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import static org.web.shop_service.product.ProductCreateRequest.*;

public record ProductUpdateRequest(
        @Size(min = MIN_SIZE_NAME, message = INVALID_SIZE_NAME)
        @NotBlank(message = INVALID_NOT_BLANK_NAME)
        String name,
        @Positive(message = INVALID_DECIMAL_MIN_PRICE)
        BigDecimal price
) {
}
