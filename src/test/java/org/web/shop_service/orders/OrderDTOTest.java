package org.web.shop_service.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web.shop_service.orders.support.TestOrderFactory;
import org.web.shop_service.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderDTOTest {

    @Test
    @DisplayName("Конвертация из сущности Order в DTO")
    void shouldConvertFromEntityToDTO() {
        // Given
        final var user = new User();
        user.setId(1L);
        final var order = TestOrderFactory.order(1L, user);

        // Action
        final var dto = new OrderDTO(order);

        // Check
        final var expectedItems = order.getItems();
        final var actualItems = dto.getItems();
        assertThat(dto.getId()).isEqualTo(order.getId());
        assertThat(dto.getStatus()).isEqualByComparingTo(order.getStatus());
        assertThat(dto.getCreatedAt()).isEqualTo(order.getCreatedAt().toString());
        assertThat(dto.getVersion()).isEqualTo(order.getVersion());
        assertThat(actualItems).hasSize(expectedItems.size());
        assertThat(dto.getCount()).isEqualTo(expectedItems.size());
    }

    @Test
    @DisplayName("Если позиций товаров нет - конвертируем заказ с пустым списком позиций")
    void whenItemsIsEmpty_shouldConvertFromEntityToDTOWithoutItems() {
        // Given
        final var user = new User();
        user.setId(1L);
        final var order = TestOrderFactory.orderWithoutItems(1L, user);

        // Action
        final var dto = new OrderDTO(order);

        // Check
        final var actualItems = dto.getItems();
        assertThat(dto.getId()).isEqualTo(order.getId());
        assertThat(dto.getStatus()).isEqualByComparingTo(order.getStatus());
        assertThat(dto.getVersion()).isEqualTo(order.getVersion());
        assertThat(actualItems).isNotNull();
        assertThat(actualItems).isEmpty();
        assertThat(dto.getCount()).isEqualTo(0);
    }
}
