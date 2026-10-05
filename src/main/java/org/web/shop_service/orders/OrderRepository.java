package org.web.shop_service.orders;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(Order.GRAPH_WITH_ITEMS_WITHOUT_PRODUCT)
    @Query("select o from Order o where o.user.id = :user_id")
    List<Order> findAllByUserId(@Param("user_id") Long userId, Pageable pageable);
}
