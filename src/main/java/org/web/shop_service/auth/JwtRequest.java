package org.web.shop_service.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание JWT")
public record JwtRequest(
        @Size(min = 3, max = 100, message = "username must be 3–100 characters long")
        @NotBlank(message = "username must not be empty")
        @Schema(description = "Имя пользователя", example = "User",
                minimum = "3", maximum = "100")
        String username,
        @Size(min = 6, max = 100, message = "password must be 6–100 characters long")
        @NotBlank(message = "password must not be empty")
        @Schema(description = "Пароль", example = "password",
                minimum = "6", maximum = "100")
        String password) {
}
