package org.web.shop_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.web.shop_service.utils.JwtUtils;

import java.time.Duration;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@Import({AuthenticationConfig.class, PasswordConfig.class})
public class SecurityConfig {
    @Bean
    @Profile("!dev")
    public SecurityFilterChain securityFilterChainNonDev(HttpSecurity http,
                        DaoAuthenticationProvider provider,
                        JwtAuthenticationFilter jwtFilter,
                        UnauthorizedEntryPoint unauthorizedEntryPoint) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/auth/**")
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
    public JwtUtils jwtUtils(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.lifetime}") Duration jwtLifetime) {
        return new JwtUtils(secret, jwtLifetime);
    }
}
