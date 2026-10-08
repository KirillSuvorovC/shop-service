package org.web.shop_service.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web.shop_service.utils.VersionedRequest;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на создание заказа")
public class OrderUpdateStatusRequest extends VersionedRequest {
    public static final String PATTERN_STATUS = "(NEW|SHIPPED|DELIVERED|CANCELLED)";
    public static final String INVALID_STATUS = "status: expected NEW, SHIPPED, DELIVERED or CANCELLED";
    public static final String EMPTY_STATUS = "status: is required";

    @NotNull(message = EMPTY_STATUS)
    @Pattern(regexp = PATTERN_STATUS, message = INVALID_STATUS)
    @Schema(title = "Статус заказа", type = "OrderStatus",
            example = "CANCELLED",
            allowableValues = {"NEW", "SHIPPED", "DELIVERED", "CANCELLED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String status;

}
