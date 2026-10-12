package org.web.shop_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.utils.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

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

        final AuthUser authUser;
        try {
            authUser = jwtUtils.getUserFromToken(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
        if(authUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final var token = new UsernamePasswordAuthenticationToken(
                    authUser,null, authUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
