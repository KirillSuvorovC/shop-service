package org.web.shop_service.auth;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.support.RestControllerTest;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.utils.JsonUtils;
import org.web.shop_service.utils.JwtUtils;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

import java.time.Duration;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestControllerTest(controllers = AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthService authService;

    @Nested
    @DisplayName("Регистрация")
    class RegisterUser {
        @Test
        @DisplayName("Вернуть 201 и Cookie с JWT, если пользователя с именем нет в БД")
        void whenUserNotFound_return201AndCookieWithJwt() throws Exception {
            // Given
            final var mockResponse = new JwtResponse("JWT");
            final long jwtLifetimeOfMinutes = Duration.ofMinutes(15)
                    .getSeconds();
            final var request = new UserRegisterRequest(
                    "username",
                    "password",
                    "password");
            when(authService.registerUser(any())).thenReturn(mockResponse);
            when(jwtUtils.getJwtLifetimeOfSeconds()).thenReturn(jwtLifetimeOfMinutes);

            // Action
            final String cookieName = "token";
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request)))
                    // Check
                    .andExpect(status().isCreated())
                    .andExpect(cookie().value(cookieName, mockResponse.token()))
                    .andExpect(cookie().maxAge(cookieName, (int) jwtLifetimeOfMinutes))
                    .andExpect(cookie().httpOnly(cookieName, true));
        }
        @Test
        @DisplayName("Вернуть 409, если пользователь с именем существует в БД")
        void whenThrowUserAlreadyExistsException_return409() throws Exception {
            // Given
            final var request = new UserRegisterRequest(
                    "username",
                    "password",
                    "no-password");
            when(authService.registerUser(any())).thenThrow(UserAlreadyExistsException.class);
            // Action
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request)))
                    // Check
                    .andExpect(status().isConflict());
        }
        @Test
        @DisplayName("Вернуть 400, если пароли не совпадают")
        void whenThrowInvalidRequestException_return400() throws Exception {
            // Given
            final var request = new UserRegisterRequest(
                    "username",
                    "password",
                    "no-password");
            when(authService.registerUser(any())).thenThrow(InvalidRequestException.class);
            // Action
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request)))
                    // Check
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Аутентификация")
    class CreateAuthToken {
        @Test
        @DisplayName("Вернуть 200 и Cookie с JWT, если введен правильный логин и пароль")
        void shouldReturn200AndCookieWithJwt() throws Exception {
            // Given
            final var mockResponse = new JwtResponse("JWT");
            final long jwtLifetimeOfMinutes = Duration.ofMinutes(15)
                    .getSeconds();
            final var request = new JwtRequest("username","password");
            when(authService.createAuthToken(any())).thenReturn(mockResponse);
            when(jwtUtils.getJwtLifetimeOfSeconds()).thenReturn(jwtLifetimeOfMinutes);

            // Action
            final String cookieName = "token";
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request)))
                    // Check
                    .andExpect(status().isOk())
                    .andExpect(cookie().value(cookieName, mockResponse.token()))
                    .andExpect(cookie().maxAge(cookieName, (int)jwtLifetimeOfMinutes))
                    .andExpect(cookie().httpOnly(cookieName, true));
        }
        @Test
        @DisplayName("Вернуть 400, если введен неправильный логин или пароль")
        void whenThrowValidationException_return400() throws Exception {
            // Given
            final var request = new JwtRequest("username","invalid");
            when(authService.createAuthToken(any())).thenThrow(ValidationException.class);

            // Action
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request)))
                    // Check
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Вернуть 400, если пользователь не найден")
        void whenThrowUsernameNotFoundException_return400() throws Exception {
            // Given
            final var request = new JwtRequest("unknown","password");
            when(authService.createAuthToken(any())).thenThrow(UsernameNotFoundException.class);

            // Action
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.asJson(request)))
                    // Check
                    .andExpect(status().isNotFound());
        }
    }
}