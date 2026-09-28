package org.web.shop_service.orders;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrderCreateRequest(
        @NotEmpty(message = INVALID_ORDER_ITEMS)
        @Size(min = 1, max = 10, message = INVALID_ORDER_ITEMS)
        Item[] items
)
{
    public static record Item(
            @NotNull(message = EMPTY_PRODUCT_ID)
            Long productId,
            @NotNull(message = EMPTY_AMOUNT)
            @Positive(message = INVALID_AMOUNT)
            Integer amount) {

    }
    public static final String INVALID_ORDER_ITEMS = "order items: expected amount of items" +
                                                                            "between 1 and 10";
    public static final String EMPTY_PRODUCT_ID = "product: is required";
    public static final String EMPTY_AMOUNT = "amount: is required";
    public static final String INVALID_AMOUNT = "amount: should be more than 0";
}
