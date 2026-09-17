package org.web.shop_service.product.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web.shop_service.outbox.Event;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class ProductPriceChangedEvent extends Event {
    private final Long productId;
    private final BigDecimal newPrice;
    private final ZonedDateTime updatedAt;
}
