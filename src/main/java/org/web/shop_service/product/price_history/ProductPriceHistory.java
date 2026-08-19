package org.web.shop_service.product.price_history;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.web.shop_service.product.Product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "products_price_history")
public class ProductPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private ZonedDateTime updatedAt;
    private BigDecimal price = new BigDecimal("0.00");
}
