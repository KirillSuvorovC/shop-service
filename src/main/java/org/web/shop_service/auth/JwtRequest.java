package org.web.shop_service.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JwtRequest(
        @Size(min = 6, message = "username must be at least 6 characters long")
        @NotBlank(message = "username must not be empty")
        String username,
        @Size(min = 8, message = "password must be at least 8 characters long")
        @NotBlank(message = "password must not be empty")
        String password) {
}
