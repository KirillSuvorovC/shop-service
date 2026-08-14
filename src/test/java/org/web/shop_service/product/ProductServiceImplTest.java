package org.web.shop_service.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.web.shop_service.utils.exceptions.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository repository;
    @Mock
    private ProductPatchValidator validator;
    @InjectMocks
    private ProductServiceImpl productService;
    @Test
    void createProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductCreateRequest(name, price);
        // Action
        final var response = productService.createProduct(request);

        // Check
        assertNotNull(response);
        assertEquals(name, response.name());
        assertEquals(0, response.price().compareTo(price));
        verify(repository, times(1)).save(any());
    }

    @Test
    void updateProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductUpdateRequest(name, price);
        final var product = new Product();
        product.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        // Action
        final var response = productService.updateProduct(1L, request);

        // Check
        assertNotNull(response);
        assertEquals(name, response.name());
        assertEquals(0, response.price().compareTo(price));
        verify(repository, times(1)).save(any());
    }

    @Test
    void patchProduct_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductPatchRequest(name, price);
        final var product = new Product();
        product.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        // Action
        final var response = productService.patchProduct(1L, request);

        // Check
        assertNotNull(response);
        assertEquals(name, response.name());
        assertEquals(0, response.price().compareTo(price));
        verify(validator, times(1)).validate(any());
    }

    @Test
    void patchProduct_whenProductNotFound_throwException() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var request = new ProductPatchRequest(name, price);
        when(repository.findById(1L)).thenThrow(EntityNotFoundException.class);
        // Action && Check
        assertThrows(EntityNotFoundException.class,
                () -> productService.patchProduct(1L, request));
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        // Given
        final BigDecimal price = new BigDecimal("50.00");
        final String name = "Product";
        final var product = new Product();
        product.setId(1L);
        product.setName(name);
        product.setPrice(price);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        // Action
        final var response = productService.getProductById(1L);

        // Check
        assertNotNull(response);
        assertEquals(name, response.name());
        assertEquals(0, response.price().compareTo(price));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getProductById_whenProductNotFound_throwException() {
        // Given
        when(repository.findById(1L)).thenThrow(EntityNotFoundException.class);
        // Action && Check
        assertThrows(EntityNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    @Test
    void getProducts_shouldReturnProductsDTO() {
        // Given
        final var products = List.of(
                createProduct(1L),
                createProduct(2L),
                createProduct(3L));
        final var page = new PageImpl<>(products);
        when(repository.findAll((Pageable)any())).thenReturn(page);
        // Action
        final var response = productService.getProducts(any());
        // Check
        assertNotNull(response);
        verify(repository, times(1)).findAll((Pageable)any());
        assertEquals(products.size(), response.getCount());
        for (int i = 0; i < products.size(); i++) {
            final var excepted = products.get(i);
            final var actual = response.getProducts().get(i);
            assertEquals(excepted.getName(), actual.name());
            assertEquals(0, actual.price().compareTo(excepted.getPrice()));
        }
    }

    @Test
    void deleteProduct() {
        // Action
        productService.deleteProduct(1L);
        // Check
        verify(repository, times(1)).deleteById(1L);
    }

    private static Product createProduct(Long id) {
        final var product = new Product();
        product.setId(id);
        product.setName("Product " + id);
        product.setPrice(new BigDecimal("50.00"));
        return product;
    }
}