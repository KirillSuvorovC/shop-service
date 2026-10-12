package org.web.shop_service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.user.support.TestUserFactory;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils("secretsecretsecretsecretsecretsecretsecret",
                Duration.ofMinutes(15));
    }

    @Test
    @DisplayName("""
            Должен сгенерировать JWT и вернуть из этого токена
            данные о пользователе в виде AuthUser
            """)
    void generateToken_shouldGenerateValidJWTAndConvertFromJWTToAuthUser()
            throws Exception {
        // Given
        final var user = TestUserFactory.defaultUserEntity();

        // Action
        final String jwt = jwtUtils.generateToken(user);

        // Check
        final AuthUser authUser = jwtUtils.getUserFromToken(jwt);
        final String resultRoleName = authUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow();

        assertThat(authUser).isNotNull();
        assertThat(authUser.getId()).isEqualTo(user.getId());
        assertThat(authUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(resultRoleName).isEqualTo(user.getRole().getName());
    }
}