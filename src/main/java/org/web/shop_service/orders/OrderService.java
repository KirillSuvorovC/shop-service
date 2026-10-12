package org.web.shop_service.orders;

import org.springframework.data.domain.Pageable;
import org.web.shop_service.user.AuthUser;

public interface OrderService {
    OrderDTO create(OrderCreateRequest request, AuthUser details);
    OrderDTO updateStatus(Long id, OrderUpdateStatusRequest request);
    OrderDTO findById(Long id);
    OrdersDTO findAllByUserId(AuthUser user, Pageable pageable);
}
