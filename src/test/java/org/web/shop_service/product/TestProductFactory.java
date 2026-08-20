package org.web.shop_service.product;

import org.web.shop_service.product.price_history.ProductPriceHistory;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TestProductFactory {
    public static Product createProduct(Long id) {
        final var product = new Product();
        product.setId(id);
        product.setName("Product " + id);
        product.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        product.setPrice(new BigDecimal("50.00"));
        return product;
    }

    public static ProductPriceHistory createProductHistory(Long id, Product product) {
        final var history = new ProductPriceHistory();
        history.setId(id);
        history.setProduct(product);
        history.setUpdatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        history.setPrice(product.getPrice());
        return  history;
    }
}
