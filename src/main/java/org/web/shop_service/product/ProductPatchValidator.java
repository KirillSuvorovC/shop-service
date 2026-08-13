package org.web.shop_service.product;

import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductPatchValidator {
    public void validate(ProductPatchRequest request) {
        final String name = request.name();
        if(name != null) {
            if(StringUtils.isBlank(name))
                throw new ValidationException(ProductCreateRequest.INVALID_NOT_BLANK_NAME);
            if(name.length() < ProductCreateRequest.MIN_SIZE_NAME)
                throw new ValidationException(ProductCreateRequest.INVALID_SIZE_NAME);
        }
        final BigDecimal newPrice = request.price();
        if(newPrice != null) {
            final BigDecimal roundedPrice = newPrice.setScale(2, RoundingMode.HALF_UP);
            if(roundedPrice.compareTo(BigDecimal.ZERO) <= 0)
                throw new ValidationException(ProductCreateRequest.INVALID_DECIMAL_MIN_PRICE);
        }
    }
}
