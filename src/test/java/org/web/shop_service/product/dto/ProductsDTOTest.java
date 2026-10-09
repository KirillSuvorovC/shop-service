package org.web.shop_service.product.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web.shop_service.product.ProductDTO;
import org.web.shop_service.product.ProductsDTO;
import org.web.shop_service.product.support.TestProductFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductsDTOTest {
    @Test
    @DisplayName("Конвертация из списка сущностей Product в DTO")
    void shouldConvertFromEntityToDTO() {
        // Given
        final var products = List.of(
                TestProductFactory.createProduct(1L),
                TestProductFactory.createProduct(2L),
                TestProductFactory.createProduct(3L)
        );

        // Action
        final var dto = new ProductsDTO(products);

        // Check
        assertThat(dto.getProducts()).hasSize(products.size());
        assertThat(dto.getCount()).isEqualTo(products.size());
    }
}
