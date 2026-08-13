package org.web.shop_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    private SecretKey secretKey;

    public String generateToken(UserDetails userDetails) {
        final var claims = new HashMap<String, Object>();
        final var roles = userDetails
                            .getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
        claims.put("roles", roles);
        final Date nowDate = new Date();
        final Date expiredDate = new Date(nowDate.getTime() + jwtLifetime.toMillis());
        final var secretKey = getSecret();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(nowDate)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaimsToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getAllClaimsToken(token).get("roles", List.class);
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
