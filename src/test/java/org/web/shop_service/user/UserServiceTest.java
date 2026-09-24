package org.web.shop_service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.web.shop_service.role.Role;
import org.web.shop_service.role.RoleService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Должен успешно создать пользователя")
    void createUser_shouldReturnUserDetails() {
        // Given
        final String encodedPassword = "encodedPassword";
        final var request = new UserRegisterRequest(
                DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
        when(roleService.getRoleByName(Role.ROLE_USER)).thenReturn(new Role(Role.ROLE_USER));
        when(encoder.encode(DEFAULT_PASSWORD)).thenReturn(encodedPassword);
        // Action
        final var userDetails = userService.createUser(request);
        final var authority = userDetails.getAuthorities()
                                         .stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .findFirst();
        // Check
        assertNotNull(userDetails);
        assertNotNull(authority);
        verify(encoder, times(1)).encode(DEFAULT_PASSWORD);
        assertEquals(DEFAULT_USERNAME, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());
        assertEquals(Role.ROLE_USER, authority.get());
    }

    @Test
    @DisplayName("Должен успешно вернуть данные пользователя")
    void loadUserByUsername_shouldReturnUserDetails() {
        // Given
        final var user = createUser();
        when(repository.findByUsername(any())).thenReturn(Optional.of(user));
        // Action
        final var userDetails = userService.loadUserByUsername(DEFAULT_USERNAME);
        final var authority = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst();
        // Check
        assertNotNull(userDetails);
        assertNotNull(authority);
        assertEquals(DEFAULT_USERNAME, userDetails.getUsername());
        assertEquals(DEFAULT_PASSWORD, userDetails.getPassword());
        assertEquals(Role.ROLE_USER, authority.get());
    }

    @Test
    @DisplayName("Если пользователь не найден - бросать исключение")
    void loadUserByUsername_whenUserNotFound_ThrowException() {
        // Given
        when(repository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());
        // Action && Check
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(DEFAULT_USERNAME));
    }

    @Test
    @DisplayName("Должен успешно вернуть пользователя")
    void findByUsername_shouldReturnUser() {
        // Given
        final var user = createUser();
        when(repository.findByUsername(any())).thenReturn(Optional.of(user));
        // Action
        final User result = userService.findByUsername(DEFAULT_USERNAME).get();
        // Check
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(DEFAULT_USERNAME, result.getUsername());
        assertEquals(DEFAULT_PASSWORD, result.getPassword());
        assertEquals(Role.ROLE_USER, result.getRole().getName());
    }

    @Test
    @DisplayName("Должен успешно вернуть несуществующего пользователя")
    void findByUsername_shouldReturnNonExistsUser() {
        // Given
        when(repository.findByUsername(any())).thenReturn(Optional.empty());
        // Action
        final Optional<User> user = userService.findByUsername("unknown");
        // Check
        assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("Должен успешно вернуть пользователя")
    void getUserByUsername_shouldReturnUser() {
        // Given
        final var user = createUser();
        when(repository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(user));
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
    @DisplayName("Если пользователь не найден - бросать исключение")
    void getUserByUsername_whenUserNotFound_ThrowException() {
        // Given
        when(repository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());
        // Action && Check
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUsername(DEFAULT_USERNAME));
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