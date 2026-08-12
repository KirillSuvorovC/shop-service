package org.web.shop_service.user;

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
    @Mock
    private UserRepository repository;
    @Mock
    private RoleService roleService;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldReturnUserDetails() {
        // Given
        final String username = "username";
        final String rawPassword = "rawPassword";
        final String encodedPassword = "encodedPassword";
        final var request = new UserRegisterRequest(
                username, rawPassword, rawPassword);
        when(roleService.getRoleByName(any())).thenReturn(new Role(Role.ROLE_USER));
        when(encoder.encode(any())).thenReturn(encodedPassword);
        // Action
        final var userDetails = userService.createUser(request);
        final var authority = userDetails.getAuthorities()
                                         .stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .findFirst();
        // Check
        assertNotNull(userDetails);
        assertNotNull(authority);
        verify(encoder, times(1)).encode(rawPassword);
        assertEquals(username, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());
        assertEquals(Role.ROLE_USER, authority.get());
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        // Given
        final String username = "username";
        final String password = "password";
        final var user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(new Role(Role.ROLE_USER));
        when(repository.findByUsername(any())).thenReturn(Optional.of(user));
        // Action
        final var userDetails = userService.loadUserByUsername(username);
        final var authority = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst();
        // Check
        assertNotNull(userDetails);
        assertNotNull(authority);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertEquals(Role.ROLE_USER, authority.get());
    }

    @Test
    void loadUserByUsername_whenUserNotFound_ThrowException() {
        // Given
        when(repository.findByUsername(any())).thenReturn(Optional.empty());
        // Action && Check
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown"));
    }

    @Test
    void findByUsername_shouldReturnUser() {
        // Given
        final String username = "username";
        final String password = "password";
        final var user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(new Role(Role.ROLE_USER));

        when(repository.findByUsername(any())).thenReturn(Optional.of(user));
        // Action
        final User result = userService.findByUsername(username).get();
        // Check
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(Role.ROLE_USER, result.getRole().getName());
    }

    @Test
    void findByUsername_shouldReturnNonExistsUser() {
        // Given
        when(repository.findByUsername(any())).thenReturn(Optional.empty());
        // Action
        final Optional<User> user = userService.findByUsername("unknown");
        // Check
        assertFalse(user.isPresent());
    }
}