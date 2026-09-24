package org.web.shop_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.web.shop_service.annotations.PublicEndPoint;
import org.web.shop_service.user.UserService;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final RequestMappingHandlerMapping mapping;
    private final UserService service;

    @Bean
    @Profile("!dev")
    public SecurityFilterChain securityFilterChainNonDev(HttpSecurity http,
                        DaoAuthenticationProvider provider,
                        JwtAuthenticationFilter jwtFilter,
                        UnauthorizedEntryPoint unauthorizedEntryPoint) throws Exception {
        final String[] publicEndPoints = getPublicEndPoints();
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(publicEndPoints)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling ->
                        handling.authenticationEntryPoint(unauthorizedEntryPoint))
                .authenticationProvider(provider)
                .addFilterBefore(jwtFilter, CsrfFilter.class)
                .addFilterAfter(new CsrfCookieFilter(), ExceptionTranslationFilter.class)
                .build();
    }

    @Bean
    @Profile("dev")
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http,
                        DaoAuthenticationProvider provider,
                        JwtAuthenticationFilter jwtFilter,
                        UnauthorizedEntryPoint unauthorizedEntryPoint) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling ->
                        handling.authenticationEntryPoint(unauthorizedEntryPoint))
                .authenticationProvider(provider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
                                PasswordEncoder passwordEncoder) {
        final var provider = new DaoAuthenticationProvider(service);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
    }

    private String[] getPublicEndPoints() {
        return mapping.getHandlerMethods().entrySet()
                .stream()
                .filter(e -> e.getValue().hasMethodAnnotation(PublicEndPoint.class))
                .flatMap(e -> e.getKey().getDirectPaths().stream())
                .toList()
                .toArray(new String[0]);
    }
}
