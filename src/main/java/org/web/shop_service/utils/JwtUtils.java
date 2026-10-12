package org.web.shop_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.user.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class JwtUtils {
    private final String secret;
    private final Duration jwtLifetime;

    private SecretKey secretKey;

    public String generateToken(User user) {
        final var claims = new HashMap<String, Object>();
        final var roles = List.of(user.getRole().getName());
        claims.put("id", user.getId());
        claims.put("roles", roles);
        final Date nowDate = new Date();
        final Date expiredDate = new Date(nowDate.getTime() + jwtLifetime.toMillis());
        final var secretKey = getSecret();
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(nowDate)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Извлекает данные из JWT и возвращает пользователя
     * @param token JWT пользователя
     * @return аутентифицированного пользователя в виде {@link AuthUser}
     * @throws Exception если JWT истек или не удалось из него извлечь данные
     */
    public AuthUser getUserFromToken(String token) throws Exception {
        final var claims = getAllClaimsToken(token);
        final String username = claims.getSubject();
        final Long id = claims.get("id", Long.class);
        final List<? extends GrantedAuthority> authorities = getRoles(claims);
        return new AuthUser(id, username, authorities);
    }

    public long getJwtLifetimeOfSeconds() { return jwtLifetime.getSeconds(); }

    private List<? extends GrantedAuthority> getRoles(Claims claims) {
        final List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private Claims getAllClaimsToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecret() {
        if(secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        return secretKey;
    }
}
