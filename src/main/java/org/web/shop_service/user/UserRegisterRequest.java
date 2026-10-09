package org.web.shop_service.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static org.web.shop_service.user.UserConstants.*;

@Schema(description = "Запрос на регистрацию")
public record UserRegisterRequest(
        @Size(min = USERNAME_MIN_SIZE,
              max = USERNAME_MAX_SIZE,
              message = INVALID_USERNAME_SIZE)
        @NotBlank(message = "username must not be empty")
        @Schema(description = "Имя пользователя", example = "User")
        String username,

        @Size(min = PASSWORD_MIN_SIZE,
              max = PASSWORD_MAX_SIZE,
              message = INVALID_PASSWORD_SIZE)
        @NotBlank(message = "password must not be empty")
        @Schema(description = "Пароль", example = "password")
        String password,

        @Size(min = PASSWORD_MIN_SIZE,
              max = PASSWORD_MAX_SIZE,
              message = INVALID_PASSWORD_SIZE)
        @NotBlank(message = "password must not be empty")
        @Schema(description = "Подтверждение пароля", example = "password")
        String confirmPassword) {
}
