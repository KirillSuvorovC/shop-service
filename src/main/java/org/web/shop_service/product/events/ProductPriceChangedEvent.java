package org.web.shop_service.product.events;

import org.web.shop_service.product.Product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ProductPriceChangedEvent(Long id,
                                       BigDecimal newPrice,
                                       ZonedDateTime updatedAt) {}
