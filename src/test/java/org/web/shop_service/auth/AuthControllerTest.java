package org.web.shop_service.auth;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.web.shop_service.Application;
import org.web.shop_service.TestSecurityConfig;
import org.web.shop_service.role.Role;
import org.web.shop_service.security.SecurityConfig;
import org.web.shop_service.user.User;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.JsonUtils;
import org.web.shop_service.utils.JwtUtils;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

import java.time.Duration;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = TestSecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthService authService;

    @Test
    void registerUser_whenUserNotFound_return201AndCookieWithJwt() throws Exception {
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
                .andExpect(cookie().maxAge(cookieName, (int)jwtLifetimeOfMinutes))
                .andExpect(cookie().httpOnly(cookieName, true));
    }

    @Test
    void registerUser_whenThrowInvalidRequestException_return400() throws Exception {
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

    @Test
    void createAuthToken_shouldReturn200AndCookieWithJwt() throws Exception {
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
    void createAuthToken_whenThrowValidationException_return400() throws Exception {
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
    void createAuthToken_whenThrowUsernameNotFoundException_return400() throws Exception {
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