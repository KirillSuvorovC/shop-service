package org.web.shop_service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieHelper {
    public static void createCookieJwt(String token, long lifetimeSeconds,
                                       HttpServletResponse response) {
        final var cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)lifetimeSeconds);
        response.addCookie(cookie);
    }
}
