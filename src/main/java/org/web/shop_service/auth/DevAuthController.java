package org.web.shop_service.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.JwtUtils;

@RestController
@RequiredArgsConstructor
@Profile("dev")
@RequestMapping("/dev/api/auth")
@Tag(name = "Dev Auth",
        description = """
        Используется для быстрой проверки прав
        на эндпоинтах. Данный эндпоинт доступен
        только для разработки
        """)
public class DevAuthController {
    private final UserService service;
    private final JwtUtils jwtUtils;
    @Operation(summary = "Аутентификация как админ")
    @PostMapping("/admin")
    public void loginAsAdmin(HttpServletResponse response) {
        final var admin = service.getUserByUsername("admin");
        final var jwt = jwtUtils.generateToken(admin);
        CookieHelper.createCookieJwt(jwt, jwtUtils.getJwtLifetimeOfSeconds(), response);
    }
}
