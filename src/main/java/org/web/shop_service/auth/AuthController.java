package org.web.shop_service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web.shop_service.annotations.PublicEndPoint;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.utils.JwtUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @PublicEndPoint
    public ResponseEntity<JwtResponse> createAuthToken(@Valid @RequestBody
                                                           JwtRequest request,
                                                       HttpServletResponse response) {
        final var jwtResponse = service.createAuthToken(request);
        createCookieToken(jwtResponse, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @PublicEndPoint
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody
                                                        UserRegisterRequest request,
                                                    HttpServletResponse response) {
        final var jwtResponse = service.registerUser(request);
        createCookieToken(jwtResponse, response);
        return ResponseEntity.ok().build();
    }

    private void createCookieToken(JwtResponse jwtResponse, HttpServletResponse response) {
        final var cookie = new Cookie("token", jwtResponse.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)jwtUtils.getJwtLifetimeOfSeconds());
        response.addCookie(cookie);
    }
}
