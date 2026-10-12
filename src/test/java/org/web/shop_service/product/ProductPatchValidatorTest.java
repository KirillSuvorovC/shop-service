package org.web.shop_service.product;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class ProductPatchValidatorTest extends MockitoExtension {
    @InjectMocks
    private ProductPatchValidator validator;
    @ParameterizedTest
    @MethodSource("validArguments")
    @DisplayName("Не бросает исключение, если запрос корректный")
    void validate_whenRequestIsValid_shouldNotThrowException(String name,
                                                             BigDecimal price) {
        // Given
        final var request = new ProductPatchRequest(name, price);
        // Action && Check
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @ParameterizedTest
    @MethodSource("invalidArguments")
    @DisplayName("Бросает исключение, если запрос некорретный")
    void validate_whenInvalidRequest_throwException(String name,
                                                    BigDecimal price) {
        // Given
        final var request = new ProductPatchRequest(name, price);
        // Action && Check
        assertThrows(ValidationException.class,
                () -> validator.validate(request));
    }

    static Stream<Arguments> validArguments() {
        return Stream.of(
                arguments("Product", new BigDecimal("50.00")),
                arguments(null, new BigDecimal("50.00")),
                arguments("Product", null),
                arguments(null, null)
        );
    }

    static Stream<Arguments> invalidArguments() {
        final var priceMoreMaximum = BigDecimal.valueOf(
                ProductConstants.MAX_PRICE + 1,
                ProductConstants.FRACTION_PRICE);
        final String longestName = "P".repeat(ProductConstants.MAX_SIZE_NAME + 1);

        return Stream.of(
                arguments("Product", priceMoreMaximum),
                arguments(longestName, new BigDecimal("50.00")),
                arguments("Product", new BigDecimal("-1")),
                arguments("Product", new BigDecimal("0.001")),
                arguments("n", new BigDecimal("50.00")),
                arguments("n", new BigDecimal("-1")),
                arguments(null, new BigDecimal("-1")),
                arguments("n", null)
        );
    }
}