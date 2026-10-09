package org.web.shop_service.auth;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.web.shop_service.role.Role;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.JwtUtils;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthServiceImpl authService;

    @Nested
    @DisplayName("Регистрация")
    class RegisterUser {
        @Test
        @DisplayName("Создать пользователя и вернуть JWT")
        void shouldCreateUserAndReturnJwt() {
            // Given
            final String username = "username";
            final String password = "password";
            final String jwt = "JsonWebToken";
            final var request = new UserRegisterRequest(username, password, password);
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    username,
                    password,
                    List.of(new SimpleGrantedAuthority(Role.ROLE_USER)));

            when(userService.existsByUsername(username)).thenReturn(false);
            when(userService.createUser(request)).thenReturn(userDetails);
            when(jwtUtils.generateToken(userDetails)).thenReturn(jwt);
            // Action
            final var response = authService.registerUser(request);

            // Check
            assertNotNull(response);
            assertEquals(jwt, response.token());
        }

        @Test
        @DisplayName("Бросить исключение, если пароли не совпадают")
        void whenPasswordsNotMatch_throwException() {
            // Given
            final String username = "username";
            final String password = "password";
            final String confirmPassword = "confirmPassword";
            final var request = new UserRegisterRequest(username, password, confirmPassword);
            // Action && Check
            assertThrows(InvalidRequestException.class, () -> authService.registerUser(request));
        }

        @Test
        @DisplayName("Бросить исключение, если пользователь уже существует")
        void whenUserAlreadyExists_throwException() {
            // Given
            final String username = "username";
            final String password = "password";
            final var request = new UserRegisterRequest(username, password, password);
            when(userService.existsByUsername(username)).thenReturn(true);
            // Action && Check
            assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(request));
        }
    }

    @Nested
    @DisplayName("Аутентификация")
    class CreateAuthToken {
        @Test
        @DisplayName("Вернуть JWT")
        void shouldReturnJwt() {
            // Given
            final String username = "username";
            final String password = "password";
            final String jwt = "JsonWebToken";
            final var request = new JwtRequest(username, password);
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    username,
                    password,
                    List.of(new SimpleGrantedAuthority(Role.ROLE_USER)));

            when(userService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtUtils.generateToken(userDetails)).thenReturn(jwt);

            // Action
            final var response = authService.createAuthToken(request);
            // Check
            assertNotNull(response);
            assertEquals(jwt, response.token());
        }

        @Test
        @DisplayName("Бросить исключение, если пользователь не найден")
        void whenUserServiceThrowException_rethrow() {
            // Given
            final var request = new JwtRequest("unknown", "password");
            when(userService.loadUserByUsername(any())).thenThrow(UsernameNotFoundException.class);

            // Action && Check
            assertThrows(UsernameNotFoundException.class, () -> authService.createAuthToken(request));
        }

        @Test
        @DisplayName("Бросить исключение, если неправильный логин или пароль")
        void whenInvalidLoginOrPassword_throwException() {
            // Given
            final var request = new JwtRequest("unknown", "password");
            when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

            // Action && Check
            assertThrows(ValidationException.class, () -> authService.createAuthToken(request));
        }
    }
}