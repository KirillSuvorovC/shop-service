package org.web.shop_service.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @Size(min = 3, max = 100, message = "username must be 3–100 characters long")
        @NotBlank(message = "username must not be empty")
        String username,
        @Size(min = 6, max = 100, message = "password must be 6–100 characters long")
        @NotBlank(message = "password must not be empty")
        String password,
        @Size(min = 6, max = 100, message = "password must be 6–100 characters long")
        @NotBlank(message = "password must not be empty")
        String confirmPassword) {
}
