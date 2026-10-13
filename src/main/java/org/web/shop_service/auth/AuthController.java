package org.web.shop_service.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.utils.JwtUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Registration/Authentication")
public class AuthController {
    private final AuthService service;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Аутентификация", description = "Возвращает JWT в Cookie с именем `token`")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                headers = @Header(name = "Set-Cookie", description = "JWT cookie",
                schema = @Schema(type = "string", example = "jwt=eyJ...; Path=/; HttpOnly"))),
        @ApiResponse(responseCode = "400", description = "Неверный логин или пароль")
    })
    public void createAuthToken(@Valid @RequestBody JwtRequest request,
                                                    HttpServletResponse response) {
        final var jwtResponse = service.createAuthToken(request);
        CookieHelper.createCookieJwt(jwtResponse.token(),
                                     jwtUtils.getJwtLifetimeOfSeconds(),
                                     response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация", description = """
            Создаёт пользователя и возвращает
            JWT в Cookie с именем `token`
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Успешная регистрация",
                    headers = @Header(name = "Set-Cookie", description = "JWT cookie",
                    schema = @Schema(type = "string", example = "token=eyJ...; Path=/; HttpOnly"))),
            @ApiResponse(responseCode = "400", description = "Пароли не совпадают"),
            @ApiResponse(responseCode = "409",
                    description = "Пользователь с таким именем уже существует"),
    })
    public void registerUser(@Valid @RequestBody UserRegisterRequest request,
                                                 HttpServletResponse response) {
        final var jwtResponse = service.registerUser(request);
        CookieHelper.createCookieJwt(jwtResponse.token(),
                                     jwtUtils.getJwtLifetimeOfSeconds(),
                                     response);
    }
}
