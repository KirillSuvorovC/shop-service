package org.web.shop_service.orders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.web.shop_service.user.User;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NamedEntityGraph(
        name = Order.GRAPH_WITH_ITEMS_WITHOUT_PRODUCT,
        attributeNodes = @NamedAttributeNode("items")
)
public class Order {
    public static final String GRAPH_WITH_ITEMS_WITHOUT_PRODUCT = "ITEMS_WITHOUT_PRODUCT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp(source = SourceType.DB)
    private ZonedDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Version
    private Long version;
}
