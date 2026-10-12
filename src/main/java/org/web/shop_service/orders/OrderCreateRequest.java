package org.web.shop_service.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Запрос на создание заказа")
public record OrderCreateRequest(
        @NotEmpty(message = INVALID_ORDER_ITEMS)
        @Size(min = 1, max = 10, message = INVALID_ORDER_ITEMS)
        @Valid
        @Schema(description = "Список позиций заказа")
        List<Item> items
)
{
    @Schema(title = "OrderCreateRequest.Item")
    public record Item(
            @NotNull(message = EMPTY_PRODUCT_ID)
            @Schema(description = "ID продукта", example = "1")
            Long productId,
            @NotNull(message = EMPTY_AMOUNT)
            @Positive(message = INVALID_AMOUNT)
            @Schema(description = "Количество продукта", example = "1", minimum = "1")
            Integer amount) {

    }
    public static final String INVALID_ORDER_ITEMS = "order items: expected amount of items" +
                                                                            "between 1 and 10";
    public static final String EMPTY_PRODUCT_ID = "product: is required";
    public static final String EMPTY_AMOUNT = "amount: is required";
    public static final String INVALID_AMOUNT = "amount: should be more than 0";
}
