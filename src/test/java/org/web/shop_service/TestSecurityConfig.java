package org.web.shop_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.web.shop_service.user.CurrentUserProvider;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.JwtUtils;

import static org.mockito.Mockito.mock;

// TODO: реализовать позже другую тестовую конфигурацию
//  с моком UserService для проверки прав пользователя
//  через @PreAuthorize, а сейчас этот класс - временное
//  решение для старых контроллеров
@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)
                .build();
    }

    @Bean
    public JwtUtils jwtUtils() { return new JwtUtils(); }

    @Bean
    @Primary
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    @Primary
    public CurrentUserProvider currentUserProvider() {
        return mock(CurrentUserProvider.class);
    }
}
