package org.web.shop_service.product;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductPatchValidatorTest extends MockitoExtension {
    @InjectMocks
    private ProductPatchValidator validator;
    @Test
    void validate_whenRequestIsValid_shouldNotThrowException() {
        // Given
        final var request = new ProductPatchRequest("Valid Name",
                                                  new BigDecimal("50.00"));
        // Action && Check
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void validate_whenNameIsInvalid_throwException() {
        invokeValidate("IN", "50.00");
    }

    @Test
    void validate_whenPriceLessOrEqualZero_throwException() {
        // Case 1:
        invokeValidate("Valid Name", "0.00");
        // Case 2:
        invokeValidate("Valid Name", "0.001");
        // Case 3:
        invokeValidate("Valid Name", "-1");
    }

    private void invokeValidate(String name, String price) {
        // Given
        final var request = new ProductPatchRequest(name, new BigDecimal(price));
        // Action && Check
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }
}