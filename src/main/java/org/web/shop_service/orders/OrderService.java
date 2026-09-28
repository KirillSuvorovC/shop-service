package org.web.shop_service.orders;

import org.springframework.data.domain.Pageable;
import org.web.shop_service.user.CurrentUser;

public interface OrderService {
    OrderDTO create(OrderCreateRequest request, CurrentUser details);
    OrderDTO updateStatus(Long id, OrderUpdateStatusRequest request);
    OrderDTO findById(Long id);
    OrdersDTO findAllByUserId(CurrentUser user, Pageable pageable);
}
