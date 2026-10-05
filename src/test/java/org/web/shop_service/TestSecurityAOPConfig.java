package org.web.shop_service;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.web.shop_service.security.SecurityConfig;
import org.web.shop_service.user.UserService;

import static org.mockito.Mockito.mock;

/**
 * Используется для тестирования проверки прав через
 * аннотации {@link PreAuthorize}, {@link PostAuthorize} и т.п.
 */
@Import({SecurityConfig.class})
@ImportAutoConfiguration(AopAutoConfiguration.class)
@TestConfiguration
public class TestSecurityAOPConfig {
    @Bean
    @Primary
    public UserService userService() {
        return mock(UserService.class);
    }
}
