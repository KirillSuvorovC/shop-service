package org.web.shop_service.product;

import lombok.Getter;
import org.web.shop_service.utils.VersionedRequest;

import java.math.BigDecimal;

@Getter
public class ProductPatchRequest extends VersionedRequest {
    private final String name;
    private final BigDecimal price;

    public ProductPatchRequest(
            String name,
            BigDecimal price
    ) {
        this.name = name;
        this.price = price;
    }
}
