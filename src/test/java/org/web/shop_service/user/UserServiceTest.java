package org.web.shop_service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.role.Role;
import org.web.shop_service.role.RoleService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тесты отсутствуют для метода {@link UserService#existsByUsername(String)}.
 * Причина: простая делегация метода класса {@link UserRepository}
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    public static final String DEFAULT_USERNAME = "username";
    public static final String DEFAULT_PASSWORD = "password";
    @Mock
    private UserRepository repository;
    @Mock
    private RoleService roleService;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private UserService userService;
    @Nested
    @DisplayName("Загрузка пользователя по имени")
    class CreateUser {
        @Test
        @DisplayName("Должен успешно создать пользователя и вернуть его детали")
        void shouldReturnUserDetails() {
            // Given
            final String encodedPassword = "encodedPassword";
            final var request = new UserRegisterRequest(
                    DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            when(repository.existsByUsername(DEFAULT_USERNAME)).thenReturn(false);
            when(roleService.getRoleByName(Role.ROLE_USER)).thenReturn(new Role(Role.ROLE_USER));
            when(encoder.encode(DEFAULT_PASSWORD)).thenReturn(encodedPassword);
            // Action
            final var user = userService.createUser(request);
            final var roleName = user.getRole().getName();
            // Check
            assertNotNull(user);
            assertNotNull(roleName);
            verify(encoder, times(1)).encode(DEFAULT_PASSWORD);
            assertEquals(DEFAULT_USERNAME, user.getUsername());
            assertEquals(encodedPassword, user.getPassword());
            assertEquals(Role.ROLE_USER, roleName);
        }
        @Test
        @DisplayName("Если пользователь существует - бросить исключение")
        void whenUserExists_throwException() {
            // Given
            final var request = new UserRegisterRequest(
                    DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            when(repository.existsByUsername(DEFAULT_USERNAME)).thenReturn(true);
            // Action && Check
            assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        }
    }

    @Nested
    @DisplayName("Загрузка пользователя по имени")
    class LoadUserByUsername {
        @Test
        @DisplayName("Вернуть существующие данные пользователя")
        void shouldReturnUserDetails() {
            // Given
            final var user = createUser();
            when(repository.findByUsernameWithRole(any())).thenReturn(Optional.of(user));
            // Action
            final var userDetails = userService.loadUserByUsername(DEFAULT_USERNAME);
            final var authority = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst();
            // Check
            assertNotNull(userDetails);
            assertNotNull(authority);
            assertTrue(authority.isPresent());
            assertEquals(DEFAULT_USERNAME, userDetails.getUsername());
            assertEquals(DEFAULT_PASSWORD, userDetails.getPassword());
            assertEquals(Role.ROLE_USER, authority.get());
        }

        @Test
        @DisplayName("Если пользователь не найден - бросить исключение")
        void whenUserNotFound_ThrowException() {
            // Given
            when(repository.findByUsernameWithRole(DEFAULT_USERNAME)).thenReturn(Optional.empty());
            // Action && Check
            assertThrows(UsernameNotFoundException.class,
                    () -> userService.loadUserByUsername(DEFAULT_USERNAME));
        }
    }

    @Nested
    @DisplayName("Поиск пользователя по имени")
    class GetUser {
        @Test
        @DisplayName("Вернуть существующего пользователя по имени")
        void shouldReturnUserByUsername() {
            // Given
            final var user = createUser();
            when(repository.findByUsernameWithRole(DEFAULT_USERNAME)).thenReturn(Optional.of(user));
            // Action
            final User result = userService.getUserByUsername(DEFAULT_USERNAME);
            // Check
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(DEFAULT_USERNAME, result.getUsername());
            assertEquals(DEFAULT_PASSWORD, result.getPassword());
            assertEquals(Role.ROLE_USER, result.getRole().getName());
        }

        @Test
        @DisplayName("Если пользователь по имени не найден - бросить исключение")
        void whenUserNotFoundByUsername_ThrowException() {
            // Given
            when(repository.findByUsernameWithRole(DEFAULT_USERNAME)).thenReturn(Optional.empty());
            // Action && Check
            assertThrows(UsernameNotFoundException.class,
                    () -> userService.getUserByUsername(DEFAULT_USERNAME));
        }

        @Test
        @DisplayName("Вернуть существующего пользователя по ID")
        void shouldReturnUserById() {
            // Given
            final var user = createUser();
            when(repository.findById(1L)).thenReturn(Optional.of(user));
            // Action
            final User result = userService.getUserById(1L);
            // Check
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(DEFAULT_USERNAME, result.getUsername());
            assertEquals(DEFAULT_PASSWORD, result.getPassword());
            assertEquals(Role.ROLE_USER, result.getRole().getName());
        }

        @Test
        @DisplayName("Если пользователь по ID не найден - бросить исключение")
        void getUserById_whenUserNotFound_ThrowException() {
            // Given
            when(repository.findById(1L)).thenReturn(Optional.empty());
            // Action && Check
            assertThrows(EntityNotFoundException.class,
                    () -> userService.getUserById(1L));
        }
    }

    private User createUser() {
        final var user = new User();
        user.setId(1L);
        user.setUsername(DEFAULT_USERNAME);
        user.setPassword(DEFAULT_PASSWORD);
        user.setRole(new Role(Role.ROLE_USER));
        return user;
    }
}