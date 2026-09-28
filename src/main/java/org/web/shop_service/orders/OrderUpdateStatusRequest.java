package org.web.shop_service.orders;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web.shop_service.utils.VersionedRequest;

@Getter
@RequiredArgsConstructor
public class OrderUpdateStatusRequest extends VersionedRequest {
    /**
     * Статус заказа в виде строки.
     * Допустимые значения — константы {@link OrderStatus}.
     */
    @NotNull(message = EMPTY_STATUS)
    @Pattern(regexp = "(NEW|SHIPPED|DELIVERED|CANCELLED)", message = INVALID_STATUS)
    private final String status;

    public static final String INVALID_STATUS = "status: expected NEW, SHIPPED, DELIVERED or CANCELLED";
    public static final String EMPTY_STATUS = "status: is required";
}
