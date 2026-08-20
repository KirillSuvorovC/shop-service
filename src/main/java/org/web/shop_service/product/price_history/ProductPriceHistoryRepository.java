package org.web.shop_service.product.price_history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {
    @Override
    @EntityGraph(ProductPriceHistory.GRAPH_WITH_PRODUCT)
    Page<ProductPriceHistory> findAll(Pageable pageable);
}
