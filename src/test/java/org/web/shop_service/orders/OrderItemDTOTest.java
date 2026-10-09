package org.web.shop_service.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web.shop_service.orders.support.TestOrderFactory;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderItemDTOTest {

    @Test
    @DisplayName("Конвертация из сущности OrderItem в DTO")
    void shouldSuccessConvertFromEntityToDTO() {
        // Given
        final var orderItem = TestOrderFactory.orderItem();

        // Action
        final var dto = new OrderItemDTO(orderItem);

        // Check
        assertThat(dto.id()).isEqualTo(orderItem.getId());
        assertThat(dto.productId()).isEqualTo(orderItem.getProduct().getId());
        assertThat(dto.priceAtOrder()).isEqualTo(orderItem.getPriceAtOrder());
        assertThat(dto.amount()).isEqualTo(orderItem.getAmount());
    }
}
