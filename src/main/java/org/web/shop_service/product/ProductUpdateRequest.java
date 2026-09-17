package org.web.shop_service.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.web.shop_service.utils.VersionedRequest;

import java.math.BigDecimal;

import static org.web.shop_service.product.ProductCreateRequest.*;

@Getter
public class ProductUpdateRequest extends VersionedRequest {
    @Size(min = MIN_SIZE_NAME, message = INVALID_SIZE_NAME)
    @NotBlank(message = INVALID_NOT_BLANK_NAME)
    private final String name;
    @Positive(message = INVALID_DECIMAL_MIN_PRICE)
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
