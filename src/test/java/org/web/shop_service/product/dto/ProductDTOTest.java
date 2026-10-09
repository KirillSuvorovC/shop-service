package org.web.shop_service.product.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web.shop_service.product.ProductDTO;
import org.web.shop_service.product.support.TestProductFactory;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductDTOTest {
    @Test
    @DisplayName("Конвертация из сущности Product в DTO")
    void shouldConvertFromEntityToDTO() {
        // Given
        final var product = TestProductFactory.createProduct(1L);

        // Action
        final var dto = ProductDTO.fromProduct(product);

        // Check
        assertThat(dto.getId()).isEqualTo(product.getId());
        assertThat(dto.getName()).isEqualTo(product.getName());
        assertThat(dto.getPrice()).isEqualByComparingTo(product.getPrice());
        assertThat(dto.getVersion()).isEqualTo(product.getVersion());
        assertThat(dto.getCreatedAt()).isNotNull();
    }
}
