package org.web.shop_service.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = Product.TABLE_NAME)
@SQLRestriction("archived = false")
public class Product {
    public static final String TABLE_NAME = "products";
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
    @Column(nullable = false)
    private Boolean archived;
    @Version
    private Long version;
}
