package org.web.shop_service.product;


import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.web.shop_service.TestTags;
import org.web.shop_service.support.IntegrationTest;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "insert_values.sql")
@Tag(TestTags.SMOKE)
public class ProductApiIntegrationTest extends IntegrationTest {
    @Test
    @DisplayName("GET - /api/products: должен вернуть список продуктов")
    void shouldReturnProducts() {
        // Given
        final var product = given()
                            .contentType(ContentType.JSON)
                            .when()
                            .get("/api/products")
                            .thenReturn().as(ProductsDTO.class);
        assertThat(product.getProducts()).isNotNull();
        assertThat(product.getProducts()).isNotEmpty();
    }

    @Test
    @DisplayName("GET - /api/products/{id}: должен вернуть продукт по ID")
    void shouldReturnProduct() {
        // Given
        final var product = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products/1")
                .thenReturn().as(ProductDTO.class);
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isNotEmpty();
        assertThat(product.getPrice()).isNotNull();
    }
}
