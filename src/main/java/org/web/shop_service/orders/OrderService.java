package org.web.shop_service.orders;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.user.User;

public interface OrderService {
    /**
     * Создает заказ в БД исходя из параметра {@code request}.
     * Заказ оформляется для {@link User}, определение пользователя
     * осуществляется по полю {@code currentUser.id}.
     * После создания заказ имеет статус {@code OrderStatus.NEW}.
     * @param request данные для создания заказа
     * @param authUser текущий аутентифицированный пользователь
     * @see OrderStatus
     * @see OrderCreateRequest
     * @throws EntityNotFoundException если продукт или пользователь не найден
     * @return созданный заказ в виде {@link OrderDTO}
     */
    OrderDTO create(OrderCreateRequest request, AuthUser authUser);
    /**
     * @param id идентификатор заказа
     * @param request данные для изменения статуса
     * @throws EntityNotFoundException если заказ не найден
     * @throws OptimisticLockingFailureException если данные заказа устарели
     * @return обновленный заказ в виде {@link OrderDTO}
     */
    OrderDTO updateStatus(Long id, OrderUpdateStatusRequest request);
    OrderDTO findById(Long id);
    OrdersDTO findAllByUserId(AuthUser user, Pageable pageable);
}
