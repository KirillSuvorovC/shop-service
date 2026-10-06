package org.web.shop_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.web.shop_service.user.CurrentUserProvider;
import org.web.shop_service.utils.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CurrentUserProvider provider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                            throws ServletException, IOException {
        String jwt = null;
        final Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (final var cookie : cookies) {
                if("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if(jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String username;
        final List<SimpleGrantedAuthority> authorities;
        try {
            username = jwtUtils.getUsername(jwt);
            // TODO: Позже убрать метод jwtUtils.getRoles(...), так как
            // появилась замена: метод CurrentUserProvider.getByUsername(username),
            // при этом возвращает CurrentUser и там уже есть поле authorities
            authorities = jwtUtils.getRoles(jwt)
                                            .stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final var user = provider.getByUsername(username);
            final var token = new UsernamePasswordAuthenticationToken(
                    user,null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
