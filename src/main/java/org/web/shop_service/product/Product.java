package org.web.shop_service.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false, unique = true)
    private Long id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false)
    private BigDecimal price = new BigDecimal("0.00");
    @Column(name = "created_at", columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;
    @Version
    private Long version;
}
