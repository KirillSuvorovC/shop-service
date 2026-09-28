package org.web.shop_service.orders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.web.shop_service.product.Product;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer amount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAtOrder;
}
