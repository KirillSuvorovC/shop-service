package org.web.shop_service.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(Order.GRAPH_WITH_ITEMS_WITHOUT_PRODUCT)
    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
