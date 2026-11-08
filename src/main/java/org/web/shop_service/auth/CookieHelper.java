package org.web.shop_service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieHelper {
    public static final String COOKIE_NAME_JWT = "token";
    public static void createCookieJwt(String token, long lifetimeSeconds,
                                       HttpServletResponse response) {
        final var cookie = new Cookie(COOKIE_NAME_JWT, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)lifetimeSeconds);
        response.addCookie(cookie);
    }
}
