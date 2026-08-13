package org.web.shop_service.product;

import java.math.BigDecimal;

public record ProductPatchRequest(
        String name,
        BigDecimal price
) {
}
