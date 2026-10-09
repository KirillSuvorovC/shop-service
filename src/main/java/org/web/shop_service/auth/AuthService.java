package org.web.shop_service.auth;

import jakarta.validation.ValidationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

public interface AuthService {
    /**
     * Аутентифицирует пользователя и генерирует JWT.
     * @throws ValidationException неправильный логин или пароль
     * @throws UsernameNotFoundException если пользователь не найден
     * @param request - данные для аутентификации
     * @return JWT, сгенерированный после регистрации пользователя
     */
    JwtResponse createAuthToken(JwtRequest request);
    /**
     * Регистрирует нового пользователя, сохраняет в БД.
     * @throws InvalidRequestException если пароли не совпадают
     * @throws UserAlreadyExistsException если пользователь уже существует в БД
     * @param request - данные для регистрации пользователя
     * @return JWT, сгенерированный после регистрации пользователя
     */
    JwtResponse registerUser(UserRegisterRequest request);
}
