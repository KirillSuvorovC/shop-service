package org.web.shop_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.web.shop_service.utils.JwtUtils;
import org.web.shop_service.utils.ResponseError;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final List<SimpleGrantedAuthority> authorities;
        try {
            jwt = authHeader.substring(7);
            username = jwtUtils.getUsername(jwt);
            authorities = jwtUtils.getRoles(jwt)
                                            .stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            final String body = ResponseError.asJson(HttpStatus.UNAUTHORIZED,
                    "JWT expired or invalid.");
            response.getWriter().write(body);
            return;
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final var token = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
