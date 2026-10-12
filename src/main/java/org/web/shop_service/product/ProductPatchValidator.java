package org.web.shop_service.product;

import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductPatchValidator {
    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(ProductConstants.MAX_PRICE,
                                                            ProductConstants.FRACTION_PRICE);
    public void validate(ProductPatchRequest request) {
        final String name = request.getName();
        if(name != null) {
            if(StringUtils.isBlank(name))
                throw new ValidationException(ProductConstants.INVALID_NOT_BLANK_NAME);
            if(name.length() < ProductConstants.MIN_SIZE_NAME ||
               name.length() > ProductConstants.MAX_SIZE_NAME)
                throw new ValidationException(ProductConstants.INVALID_SIZE_NAME);
        }
        final BigDecimal newPrice = request.getPrice();
        if(newPrice != null) {
            final BigDecimal roundedPrice = newPrice.setScale(ProductConstants.FRACTION_PRICE,
                                                              RoundingMode.HALF_UP);
            if(roundedPrice.compareTo(BigDecimal.ZERO) <= 0 ||
               roundedPrice.compareTo(MAX_PRICE) >= 1)
                throw new ValidationException(ProductConstants.INVALID_PRICE);
        }
    }
}
